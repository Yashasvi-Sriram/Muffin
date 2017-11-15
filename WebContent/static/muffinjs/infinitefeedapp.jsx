let getTimestamp = function (localDateTime) {
    let p = moment();
    p.year(localDateTime.date.year).month(localDateTime.date.month - 1).date(localDateTime.date.day);
    p.hour(localDateTime.time.hour).minute(localDateTime.time.minute).second(localDateTime.time.second);
    return p.fromNow();
};

let Review = React.createClass({
    render: function () {
        return (
            <div className="card review hoverable">
                <div className="card-content">
                    <div>{this.props.movieName}</div>
                    <div>{this.props.muff.name} <span className="pink-text">@{this.props.muff.handle}</span></div>
                    <div className="blue-text">{getTimestamp(this.props.addedOn)}</div>
                    <br/>
                    <div className="red-text">{this.props.rating}</div>
                    <div className="flow-text">{this.props.text}</div>
                </div>
            </div>
        );
    }
});

// todo: handle new reviews and posts
window.InfiniteFeedApp = React.createClass({
    TYPES: {
        REVIEW: 0,
    },
    getInitialState: function () {
        return {
            feed: [],
            reviewOffset: 0,
        }
    },
    getDefaultProps: function () {
        return {
            id: 0,
            limit: 10,
            contextPath: '',
            reviewFetchUrl: '/review/fetch/followers',
        }
    },
    _incrementReviewOffset: function (fetchedDataLength) {
        // last batch
        if (fetchedDataLength < this.props.limit) {
            this.state.reviewOffset -= this.props.limit;
            this.state.reviewOffset += fetchedDataLength;
        }
        // update limit
        this.state.reviewOffset += this.props.limit;
    },
    fetchNextReviewBatch: function () {
        let url = this.props.contextPath + this.props.reviewFetchUrl;
        let self = this;
        $.ajax({
            url: url,
            type: 'GET',
            data: {
                id: self.props.id,
                offset: self.state.reviewOffset,
                limit: self.props.limit,
            },
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let reviews = json.data;
                    self._incrementReviewOffset(reviews.length);
                    // no results
                    if (reviews.length === 0) {
                        Materialize.toast('End of feed!', 2000);
                        return;
                    }
                    // add results
                    self.setState(ps => {
                        let feed = ps.feed;
                        feed = feed.concat(reviews.map((review, i) => {
                            return {
                                type: self.TYPES.REVIEW,
                                data: review
                            }
                        }));
                        return {feed: feed};
                    });
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    render: function () {
        let feed = this.state.feed.map(feedItem => {
            switch (feedItem.type) {
                case this.TYPES.REVIEW:
                    return <Review
                        key={feedItem.data.id}
                        id={feedItem.data.id}
                        rating={feedItem.data.rating}
                        text={feedItem.data.text}
                        addedOn={feedItem.data.addedOn}
                        movieName={feedItem.data.movieName}
                        muff={feedItem.data.muff}/>;
                    break;
                default:
                    return <div>Unknown Element</div>;
                    break;
            }
        });
        return (
            <div>
                <div ref="feed">
                    {feed}
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
