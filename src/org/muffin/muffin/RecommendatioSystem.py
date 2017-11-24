import MySQLdb
import numpy as np

db = MySQLdb.connect("jdbc:postgresql://localhost:5070/postgres","anirudh","","TESTDB")
cursor = db.cursor()
getuid = "SELECT UNIQUE muff_id from rating"
getmid = "SELECT UNIQUE movie_id from rating"
getratings = "SELECT muff_id,movie_id,rating from rating"
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
	print "Error: unable to fetch data"

epochs = 20
numfeatures = 5
alpha = 0.0001
beta = 0.01
betabias = 0.1
userfeatures = 0.1*np.ones([usertotalcount,numfeatures])
moviefeatures = 0.1*np.ones([movietotalcount,numfeatures])
userbias = 0.5*np.ones([usertotalcount])
moviebias = 0.5*np.ones([movietotalcount])
for epoch in range(epochs):
	totalerror = 0 
	for rows in ratinglist:
		userid = int(rows[0])
		movieid = int(rows[1])
		actualrating = rows[3]
		predictedrating = userbias[userid] + moviebias[movieid] + np.matmul(userfeatures[userid,:],moviefeatures[movieid,:])
		error = actualrating - predictedrating
		totalerror += error*error
		userfeatures[userid,:] += alpha*(2*error*moviefeatures[movieid,:] - beta*userfeatures[userid,:])
		moviefeatures[userid,:] += alpha*(2*error*userfeatures[movieid,:] - beta*moviefeatures[userid,:])  
		userbias[userid] += alpha*(error - betabias*userbias[userid])
		moviebias[movieid] += alpha*(error - betabias*moviebias[movieid])
	print "epoch : " + epoch + " error : " + totalerror
