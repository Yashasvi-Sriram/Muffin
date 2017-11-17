let AutoCompleteResult = React.createClass({
    getDefaultProps: function () {
        return {
            contextPath: '',
        }
    },
    render: function () {
        return (
            <div className="collection-item">
                <div>{this.props.name}</div>
            </div>
        );
    }
});

let AutoCompleteApp = React.createClass({
    getInitialState: function () {
        return {
            results: [],
            offset: 0,
        }
    },
    getDefaultProps: function () {
        return {
            limit: 5,
            contextPath: '',
            searchUrl: '',
        }
    },
    _resetOffset: function () {
        this.state.offset = 0;
    },
    _incrementOffset: function (fetchedDataLength) {
        this.state.offset += fetchedDataLength;
    },
    _decrementOffset: function () {
        let floorExcess = this.state.offset % this.props.limit;
        this.state.offset -= floorExcess;
        if (floorExcess === 0) {
            let prevOffset = this.state.offset - 2 * this.props.limit;
            this.state.offset = prevOffset < 0 ? 0 : prevOffset;
        } else {
            let prevOffset = this.state.offset - this.props.limit;
            this.state.offset = prevOffset < 0 ? 0 : prevOffset;
        }
    },
    fetchNextBatch: function (pattern) {
        let self = this;
        let url = this.props.contextPath + this.props.searchUrl;
        $.ajax({
            url: url,
            type: 'GET',
            data: {pattern: pattern, offset: self.state.offset, limit: self.props.limit},
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    self._incrementOffset(data.length);
                    // no results
                    if (data.length === 0) {
                        Materialize.toast('End of search!', 2000);
                        return;
                    }
                    // add results
                    self.setState(ps => {
                        return {results: data};
                    });
                    $(self.refs.results).show();
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    fetchPreviousBatch: function (pattern) {
        this._decrementOffset();
        if (this.state.offset === 0) {
            Materialize.toast('Start of search!', 2000);
        }
        this.fetchNextBatch(pattern);
    },
    onRegexInputKeyDown: function (e) {
        switch (e.keyCode || e.which) {
            // Enter Key
            case 13:
                let self = this;
                this._resetOffset();
                self.setState(ps => {
                    return {results: []};
                });
                this.fetchNextBatch(e.target.value);
                break;
            // Escape key
            case 27:
                $(this.refs.results).hide();
                break;
            default:
                break;
        }
    },
    render: function () {
        let results = this.state.results.map(result => {
            return <AutoCompleteResult
                key={result.id}
                id={result.id}
                name={result.name}
                contextPath={this.state.contextPath}
            />;
        });
        return (
            <div>
                <div className="row">
                    <input onKeyDown={this.onRegexInputKeyDown}
                           ref="pattern"
                           placeholder="Search" type="text" className="col s12"/>
                    <div className="col s12" ref="results">
                        <div className="collection with-header">
                            <div className="collection-header"><span className="flow-text">Results</span>
                                <span className="right">
                            <button className="btn btn-flat"
                                    onClick={e => this.fetchPreviousBatch(this.refs.pattern.value)}><i
                                className="material-icons">keyboard_arrow_left</i></button>
                            <button className="btn btn-flat"
                                    onClick={e => this.fetchNextBatch(this.refs.pattern.value)}><i
                                className="material-icons">keyboard_arrow_right</i></button>
                            </span>
                            </div>
                            {results}
                        </div>
                    </div>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        $(this.refs.results).hide();
    },
});
