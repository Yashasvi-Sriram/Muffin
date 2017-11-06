let Review = React.createClass({
    render: function () {
        return (
            <div className="card">
                <div className="card-content">
                    <div>{this.props.rating}</div>
                    <div className="pink-text">{this.props.movieName}</div>
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
            limit: 10,
            contextPath: '',
            reviewFetchUrl: '/review/fetch',
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
            data: {reviewOffset: self.state.reviewOffset, limit: self.props.limit},
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
                        Materialize.toast('End of your Review feed!', 2000);
                        return;
                    }
                    // add results
                    self.setState(ps => {
                        let feed = ps.feed;
                        feed.push(reviews.map((review, i) => {
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
                        key={feedItem.id}
                        id={feedItem.id}
                        rating={feedItem.rating}
                        movieName={feedItem.movieName}/>;
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
                <button className="btn btn-flat"
                        onClick={e => this.fetchNextReviewBatch()}>
                    <i className="material-icons">keyboard_arrow_right</i>
                </button>
            </div>
        );
    },
    componentDidMount: function () {
        this.fetchNextReviewBatch();
    },
});
