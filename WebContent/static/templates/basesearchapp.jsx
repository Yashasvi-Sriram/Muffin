let SearchResultWrapper = React.createClass({
    render: function () {
        return (
            <tr>
                <td>{this.props.name}</td>
                <td className="pink-text">{this.props.handle}</td>
            </tr>
        );
    }
});

window.BaseSearchApp = React.createClass({
    getInitialState: function () {
        return {
            results: [],
            offset: 0,
        }
    },
    getDefaultProps: function () {
        return {
            limit: 3,
            contextPath: '',
            url: '',
        }
    },
    _resetOffset: function () {
        this.state.offset = 0;
    },
    _incrementOffset: function (fetchedDataLength) {
        // last batch
        if (fetchedDataLength < this.props.limit) {
            this.state.offset -= this.props.limit;
            this.state.offset += fetchedDataLength;
        }
        // update limit
        this.state.offset += this.props.limit;
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
        let url = this.props.contextPath + this.props.url;
        let self = this;
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
            // Left key
            case 37:
                this.fetchPreviousBatch(this.refs.pattern.value);
                break;
            // Right key
            case 39:
                this.fetchNextBatch(this.refs.pattern.value);
                break;
            default:
                break;
        }
    },
    render: function () {
        let results = this.state.results.map(muff => {
            return <SearchResultWrapper
                key={muff.id}
                id={muff.id}
                name={muff.name}
                handle={muff.handle}
                level={muff.level}
            />;
        });
        return (
            <div>
                <input onKeyDown={this.onRegexInputKeyDown}
                       ref="pattern"
                       placeholder="Search" type="text"/>
                <div ref="results">
                    <button className="btn btn-flat"
                            onClick={e => this.fetchPreviousBatch(this.refs.pattern.value)}><i
                        className="material-icons">keyboard_arrow_left</i></button>
                    <button className="btn btn-flat"
                            onClick={e => this.fetchNextBatch(this.refs.pattern.value)}><i
                        className="material-icons">keyboard_arrow_right</i></button>
                    <table className="white">
                        <tbody>{results}</tbody>
                    </table>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        $(this.refs.results).hide();
    },
});
