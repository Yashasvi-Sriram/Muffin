window.ToggleFollowsApp = React.createClass({
    getInitialState: function () {
        return {
            follows: undefined,
        }
    },
    getDefaultProps: function () {
        return {
            followerId: 0,
            followeeId: 0,
            contextPath: '',
            toggleFollowsUrl: '/muff/follows/toggle',
            doesFollowsUrl: '/muff/follows/does',
        }
    },
    toggleFollows: function () {
        let url = this.props.contextPath + this.props.toggleFollowsUrl;
        let self = this;
        $.ajax({
            url: url,
            type: 'GET',
            data: {followeeId: self.props.followeeId},
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    self.setState(ps => {
                        return {follows: data};
                    });
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    getInitialFollowsState: function () {
        let url = this.props.contextPath + this.props.doesFollowsUrl;
        let self = this;
        $.ajax({
            url: url,
            type: 'GET',
            data: {followeeId: self.props.followeeId},
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    self.setState(ps => {
                        return {follows: data};
                    });
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    render: function () {
        let content;
        if (this.state.follows === undefined) {
            content = (
                <div>
                    <button className="btn btn-flat">Initializing ...
                    </button>
                </div>
            );
        } else if (this.state.follows === true) {
            content = (
                <div>
                    <div>You are currently
                        following {this.props.followerId === this.props.followeeId ? 'yourself' : 'this muff'}</div>
                    <button className="btn btn-flat red white-text waves-effect"
                            onClick={this.toggleFollows}>Un Follow
                    </button>
                </div>
            );
        } else {
            content = (
                <div>
                    <div>You are not
                        following {this.props.followerId === this.props.followeeId ? 'yourself' : 'this muff'}</div>
                    <button className="btn btn-flat teal white-text waves-effect"
                            onClick={this.toggleFollows}>Follow
                    </button>
                </div>
            );
        }
        return (
            <div className="card brown lighten-5">
                <div className="card-content">
                    {content}
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        this.getInitialFollowsState();
    },
});
