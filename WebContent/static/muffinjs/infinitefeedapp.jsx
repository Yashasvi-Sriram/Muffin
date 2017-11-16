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
            <div className="card review hoverable" ref="feedItem"
                 style={{backgroundColor: 'whitesmoke'}}
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
        $(this.refs.feedItem).fadeOut(0).fadeIn(1000);
    },
    compositionUpdate: function () {
        this.refreshLastModified();
        $(this.refs.feedItem).fadeOut(0).fadeIn(1000);
    }
});

let Seek = React.createClass({
    getInitialState: function () {
        return {
            fromTimestamp: '',
        }
    },
    refreshLastModified: function () {
        this.setState({fromTimestamp: fromNow(this.props.timestamp)});
    },
    render: function () {
        return (
            <div className="card seek hoverable pink lighten-5" ref="feedItem"
                 onMouseEnter={e => this.refreshLastModified()}>
                <div className="card-content">
                    <div>{this.props.muff.name} <span className="pink-text">@{this.props.muff.handle}</span></div>
                    <div className="blue-text">{this.state.fromTimestamp}</div>
                    <div ref="genres">Genres: {
                        this.props.genres.map(genre => {
                            return <span key={genre.id} className="teal white-text badge">{genre.name}</span>
                        })
                    }</div>
                    <br/>
                    <div className="flow-text">{this.props.text}</div>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        this.refreshLastModified();
        $(this.refs.feedItem).fadeOut(0).fadeIn(1000);
    },
    compositionUpdate: function () {
        this.refreshLastModified();
        $(this.refs.feedItem).fadeOut(0).fadeIn(1000);
    }
});

let TYPES = {
    REVIEW: 'review',
    SEEK: 'seek',
};
let POSTFIXES = {
    STATE: {
        OFFSET: 'Offset',
        FEED_HASH_MAP: 'FeedHashMap',
        LAST_SEEN: 'LastSeen',
    },
    PROPS: {
        FETCH_URL: 'FetchUrl',
        FETCH_PARAM: 'FetchParam',
        LIMIT: 'Limit',
    },
};

// todo: handle multiple types
// todo: clock sync
/**
 * This buffering model can handle
 * fetching newly created items, updated existing items as long as the a timestamp is maintained
 * But it becomes inconsistent when items can be deleted, roughly speaking some items can never be fetched due to offset problems
 * */
window.InfiniteFeedApp = React.createClass({
    getInitialState: function () {
        let ts = moment().format('YYYY-MM-DD HH:mm:ss');
        let initialState = {
            feed: [],
        };
        // REVIEW
        initialState[TYPES.REVIEW + POSTFIXES.STATE.LAST_SEEN] = ts;
        initialState[TYPES.REVIEW + POSTFIXES.STATE.OFFSET] = 0;
        initialState[TYPES.REVIEW + POSTFIXES.STATE.FEED_HASH_MAP] = {};
        // SEEK
        initialState[TYPES.SEEK + POSTFIXES.STATE.LAST_SEEN] = ts;
        initialState[TYPES.SEEK + POSTFIXES.STATE.OFFSET] = 0;
        initialState[TYPES.SEEK + POSTFIXES.STATE.FEED_HASH_MAP] = {};
        return initialState;
    },
    getDefaultProps: function () {
        let defaultProps = {
            contextPath: '',
        };
        // REVIEW
        defaultProps[TYPES.REVIEW + POSTFIXES.PROPS.LIMIT] = 10;
        defaultProps[TYPES.REVIEW + POSTFIXES.PROPS.FETCH_PARAM] = 0;
        defaultProps[TYPES.REVIEW + POSTFIXES.PROPS.FETCH_URL] = '';
        // SEEK
        defaultProps[TYPES.SEEK + POSTFIXES.PROPS.LIMIT] = 10;
        defaultProps[TYPES.SEEK + POSTFIXES.PROPS.FETCH_PARAM] = 0;
        defaultProps[TYPES.SEEK + POSTFIXES.PROPS.FETCH_URL] = '';
        return defaultProps;
    },
    fetchNextBatch: function (type) {
        let self = this;

        let url = this.props.contextPath + this.props[type + POSTFIXES.PROPS.FETCH_URL];

        let limit = this.props[type + POSTFIXES.PROPS.LIMIT];
        let param = this.props[type + POSTFIXES.PROPS.FETCH_PARAM];

        let offsetKey = type + POSTFIXES.STATE.OFFSET;
        let offset = this.state[offsetKey];

        let lastSeenKey = type + POSTFIXES.STATE.LAST_SEEN;
        let lastSeen = this.state[lastSeenKey];

        let hashMapKey = type + POSTFIXES.STATE.FEED_HASH_MAP;
        let requestTimeStamp = moment().format('YYYY-MM-DD HH:mm:ss');

        $.ajax({
            url: url,
            type: 'GET',
            data: {
                id: param,
                offset: offset,
                limit: limit,
                lastSeen: lastSeen,
            },
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let fetchedData = json.data;
                    // no results
                    if (fetchedData.length === 0) {
                        Materialize.toast('End of feed!', 2000);
                        return;
                    }
                    // add results
                    self.setState(prevState => {
                        let feed = prevState.feed;
                        let _T_HashMap = prevState[hashMapKey];
                        let noNew_T_s = 0;
                        fetchedData.forEach((fetchedItem, i) => {
                            // new fetchedItem
                            if (_T_HashMap[fetchedItem.id] === undefined) {
                                _T_HashMap[fetchedItem.id] = feed.length;
                                feed.push({
                                    type: type,
                                    data: fetchedItem
                                });
                                noNew_T_s++;
                            }
                            // old fetchedItem
                            else {
                                let existingFeedIndex = _T_HashMap[fetchedItem.id];
                                // invalidate old one
                                feed[existingFeedIndex] = undefined;
                                // add new one at end
                                _T_HashMap[fetchedItem.id] = feed.length;
                                feed.push({
                                    type: type,
                                    data: fetchedItem
                                });
                            }
                        });
                        let _T_Offset = prevState[offsetKey] + noNew_T_s;
                        // return
                        let retObject = {
                            feed: feed,
                        };
                        retObject[lastSeenKey] = requestTimeStamp;
                        retObject[offsetKey] = _T_Offset;
                        retObject[hashMapKey] = _T_HashMap;
                        return retObject;
                    });
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    fetchNextReviewBatch: function () {
        this.fetchNextBatch(TYPES.REVIEW);
    },
    fetchNextSeekBatch: function () {
        this.fetchNextBatch(TYPES.SEEK);
    },
    render: function () {
        let HTMLFeed = [];
        for (let i = 0; i < this.state.feed.length; ++i) {
            let feedItem = this.state.feed[i];
            if (feedItem === undefined) {
                continue;
            }
            switch (feedItem.type) {
                case TYPES.REVIEW:
                    HTMLFeed.push(
                        <Review
                            key={TYPES.REVIEW + '-' + feedItem.data.id}
                            id={feedItem.data.id}
                            rating={feedItem.data.rating}
                            text={feedItem.data.text}
                            lastModified={feedItem.data.lastModified}
                            movieName={feedItem.data.movieName}
                            muff={feedItem.data.muff}/>
                    );
                    break;
                case TYPES.SEEK:
                    HTMLFeed.push(
                        <Seek
                            key={TYPES.SEEK + '-' + feedItem.data.id}
                            id={feedItem.data.id}
                            muff={feedItem.data.muff}
                            text={feedItem.data.text}
                            timestamp={feedItem.data.timestamp}
                            genres={feedItem.data.genres}/>
                    );
                    break;
                default:
                    HTMLFeed.push(<div>Unknown Element</div>);
                    break;
            }
        }
        return (
            <div style={{marginBottom: '50vh'}}>
                <div ref="feed">
                    {HTMLFeed}
                </div>
                <div className="card">
                    <div className="card-content">
                        <button className="btn btn-flat" onClick={e => this.fetchNextReviewBatch()}>
                            LOAD MORE REVIEWS...
                        </button>
                        <button className="btn btn-flat" onClick={e => this.fetchNextSeekBatch()}>
                            LOAD MORE SEEKS...
                        </button>
                    </div>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        this.fetchNextReviewBatch();
        this.fetchNextSeekBatch();
    },
});
