import psycopg2
import numpy as np
try:
	db = psycopg2.connect("dbname='postgres' user='anirudh' host='localhost' password='' port=5700")
except:
	print "Could not form connection"
cursor = db.cursor()
getuid = "SELECT DISTINCT muff_id from review;"
getmid = "SELECT DISTINCT movie_id from review;"
getratings = "SELECT muff_id,movie_id,rating from review;"
forwardusermap = dict()
backwardusermap = dict()
forwardmoviemap = dict()
backwardmoviemap = dict()
usertotalcount = 0
movietotalcount = 0
ratinglist = []
try:
	cursor.execute(getuid)
	userresults = cursor.fetchall()
	for rows in userresults:
		forwardusermap[int(float(rows[0]))] = usertotalcount
		backwardusermap[usertotalcount] = int(float(rows[0]))
		usertotalcount += 1
	cursor.execute(getmid)
	movieresults = cursor.fetchall()
	for rows in movieresults:
		forwardmoviemap[int(float(rows[0]))] = movietotalcount
		backwardmoviemap[movietotalcount] = int(float(rows[0]))
		movietotalcount += 1
	cursor.execute(getratings)
	ratingresults = cursor.fetchall()
	for rows in ratingresults:
		ratinglist.append([forwardusermap[int(float(rows[0]))],forwardmoviemap[int(float(rows[1]))],float(rows[2])/10.0])
except Exception as e:
	print e

epochs = 20
numfeatures = 5
alpha = 0.001
beta = 0.01
betabias = 0.1
userfeatures = 0.1*np.ones([usertotalcount,numfeatures])
moviefeatures = 0.1*np.ones([movietotalcount,numfeatures])
userbias = 0.5*np.ones([usertotalcount])
moviebias = 0.5*np.ones([movietotalcount])
availabledict = dict()
for rows in ratinglist:
	userid = int(rows[0])
	movieid = int(rows[1])
	actualrating = rows[2]
	availabledict[str(movieid) + "-" + str(userid)] = actualrating

for epoch in range(epochs):
	totalerror = 0
	count = 0 
	for rows in ratinglist:
		count += 1
		userid = int(rows[0])
		movieid = int(rows[1])
		actualrating = rows[2]
		predictedrating = userbias[userid] + moviebias[movieid] + np.matmul(userfeatures[userid,:],moviefeatures[movieid,:])
		error = actualrating - predictedrating
		totalerror += error*error
		userfeatures[userid,:] += alpha*(2*error*moviefeatures[movieid,:] - beta*userfeatures[userid,:])
		moviefeatures[userid,:] += alpha*(2*error*moviefeatures[movieid,:] - beta*moviefeatures[userid,:])  
		userbias[userid] += alpha*(error - betabias*userbias[userid])
		moviebias[movieid] += alpha*(error - betabias*moviebias[movieid])
	print "Epoch : " + str(epoch) + " average error : " + str(totalerror/count)

useruserlist = []
usermovielist = []
for user1 in range(usertotalcount):
	helperuserlist = []
	helpermovielist = []
	for user2 in range(usertotalcount):
		if user1 != user2:
			distance = np.sum(np.square(userfeatures[user1,:] - userfeatures[user2,:]))
			helperuserlist.append([backwardusermap[user1],backwardusermap[user2],distance])
	for movie in range(movietotalcount):
		if str(user1) + "-" + str(movie) not in availabledict:
			predictedrating = userbias[user1] + moviebias[movie] + np.matmul(userfeatures[user1,:],moviefeatures[movie,:])
			helpermovielist.append([backwardusermap[user1],backwardmoviemap[movie],10.0*predictedrating])
	helperuserlist = sorted(helperuserlist, key=lambda useruser: useruser[2])
	helpermovielist = sorted(helpermovielist, key=lambda usermovie: usermovie[2],reverse=True)
	useruserlist.extend(helperuserlist[0:10]) 
	usermovielist.extend(helpermovielist[0:10]) 
clearuser = "DELETE from muff_suggestion"
clearmovie = "DELETE from movie_suggestion"
cursor.execute(clearuser)
cursor.execute(clearmovie)
cursor.executemany("INSERT INTO muffsuggestion VALUES(%s,%s,%s)", useruserlist)
cursor.executemany("INSERT INTO moviesuggestion VALUES(%s,%s,%s)", usermovielist)
db.commit()
print "Done!"