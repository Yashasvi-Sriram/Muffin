let fromNow = function (localDateTime) {
    let p = moment();
    p.year(localDateTime.date.year).month(localDateTime.date.month - 1).date(localDateTime.date.day);
    p.hour(localDateTime.time.hour).minute(localDateTime.time.minute).second(localDateTime.time.second);
    return p.fromNow();
};

let Review = React.createClass({
    getInitialState: function () {
        return {
            fromLastModified: '',
        }
    },
    refreshLastModified: function () {
        this.setState({fromLastModified: fromNow(this.props.lastModified)});
    },
    render: function () {
        return (
            <div className="card review hoverable" ref="feedItem" style={{display: 'none'}}
                 onMouseEnter={e => this.refreshLastModified()}>
                <div className="card-content">
                    <div>{this.props.muff.name} <span className="pink-text">@{this.props.muff.handle}</span></div>
                    <div>{this.props.movieName}</div>
                    <div className="blue-text">{this.state.fromLastModified}</div>
                    <br/>
                    <div className="red-text">{this.props.rating}</div>
                    <div className="flow-text">{this.props.text}</div>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        this.refreshLastModified();
        $(this.refs.feedItem).fadeIn(1500);
    }
});

// todo: handle new reviews
// todo: handle multiple types
// todo: clock sync
window.InfiniteFeedApp = React.createClass({
    TYPES: {
        REVIEW: 0,
    },
    getInitialState: function () {
        return {
            feed: [],
            reviewLastSeen: moment().format('YYYY-MM-DD HH:mm:ss'),
            reviewOffset: 0,
            reviewFeedHashMap: {},
        }
    },
    getDefaultProps: function () {
        return {
            limit: 10,
            contextPath: '',
            reviewFetchUrl: '',
            reviewFetchParam: 0,
        }
    },
    fetchNextReviewBatch: function () {
        let url = this.props.contextPath + this.props.reviewFetchUrl;
        let self = this;
        let requestTimeStamp = moment().format('YYYY-MM-DD HH:mm:ss');
        $.ajax({
            url: url,
            type: 'GET',
            data: {
                id: self.props.reviewFetchParam,
                offset: self.state.reviewOffset,
                limit: self.props.limit,
                lastSeen: self.state.reviewLastSeen,
            },
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let fetchedReviews = json.data;
                    // no results
                    if (fetchedReviews.length === 0) {
                        Materialize.toast('End of feed!', 2000);
                        return;
                    }
                    // add results
                    self.setState(prevState => {
                        let feed = prevState.feed;
                        let reviewFeedHashMap = prevState.reviewFeedHashMap;
                        let noNewReviews = 0;
                        fetchedReviews.forEach((review, i) => {
                            // new review
                            if (reviewFeedHashMap[review.id] === undefined) {
                                reviewFeedHashMap[review.id] = feed.length;
                                feed.push({
                                    type: self.TYPES.REVIEW,
                                    data: review
                                });
                                noNewReviews++;
                            }
                            // old review
                            else {
                                let existingReviewFeedIndex = reviewFeedHashMap[review.id];
                                // invalidate old one
                                feed[existingReviewFeedIndex] = undefined;
                                // add new one at end
                                reviewFeedHashMap[review.id] = feed.length;
                                feed.push({
                                    type: self.TYPES.REVIEW,
                                    data: review
                                });
                            }
                        });
                        let reviewOffset = prevState.reviewOffset + noNewReviews;
                        console.log(reviewOffset);
                        Materialize.toast('Got some feed!', 2000);
                        return {
                            feed: feed,
                            reviewLastSeen: requestTimeStamp,
                            reviewOffset: reviewOffset,
                            reviewFeedHashMap: reviewFeedHashMap
                        };
                    });
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    render: function () {
        let HTMLFeed = [];
        for (let i = 0; i < this.state.feed.length; ++i) {
            let feedItem = this.state.feed[i];
            if (feedItem === undefined) {
                continue;
            }
            switch (feedItem.type) {
                case this.TYPES.REVIEW:
                    HTMLFeed.push(
                        <Review
                            key={feedItem.data.id}
                            id={feedItem.data.id}
                            rating={feedItem.data.rating}
                            text={feedItem.data.text}
                            lastModified={feedItem.data.lastModified}
                            movieName={feedItem.data.movieName}
                            muff={feedItem.data.muff}/>
                    );
                    break;
                default:
                    HTMLFeed.push(<div>Unknown Element</div>);
                    break;
            }
        }
        return (
            <div>
                <div ref="feed">
                    {HTMLFeed}
                </div>
                <div className="card"
                     onClick={e => this.fetchNextReviewBatch()}>
                    <div className="card-content">
                        <div className="card-title">
                            LOAD MORE ...
                        </div>
                    </div>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        this.fetchNextReviewBatch();
    },
});
