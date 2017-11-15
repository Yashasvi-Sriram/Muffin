window.ToggleLikesApp = React.createClass({
    getInitialState: function () {
        return {
            likes: undefined,
            count: 0,
        }
    },
    getDefaultProps: function () {
        return {
            actorId: 0,
            muffId: 0,
            actorName: '',
            contextPath: '',
            toggleLikesUrl: '/actor/likes/toggle',
            doesLikesUrl: '/actor/likes/does',
            countLikesUrl: '/actor/likes/count',
        }
    },
    toggleLikes: function () {
        let url = this.props.contextPath + this.props.toggleLikesUrl;
        let self = this;
        $.ajax({
            url: url,
            type: 'GET',
            data: {actorId: self.props.actorId},
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    self.setState(ps => {
                        let countToggle = (data === true ? ps.count + 1 : ps.count - 1);
                        return {likes: data, count: countToggle};
                    });
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    getInitialLikesState: function () {
        let url = this.props.contextPath + this.props.doesLikesUrl;
        let self = this;
        $.ajax({
            url: url,
            type: 'GET',
            data: {actorId: self.props.actorId},
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    self.setState(ps => {
                        return {likes: data};
                    });
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },

    getInitialLikesCount: function () {
        let url = this.props.contextPath + this.props.countLikesUrl;

        let self = this;
        $.ajax({
            url: url,
            type: 'GET',
            data: {actorId: self.props.actorId},
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    self.setState(ps => {
                        return {count: data};
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
        if (this.state.likes === undefined) {
            content = (
                <div>
                    <button className="btn btn-flat">Initializing ...
                    </button>
                </div>
            );
        } else if (this.state.likes === true) {
            content = (
                <div>
                    <div>You are currently
                        liking {this.props.actorName}</div>
                    <button className="btn btn-flat red white-text waves-effect"
                            onClick={this.toggleLikes}>Un Like
                    </button>
                </div>
            );
        } else {
            content = (
                <div>
                    <div>You are not
                        liking {this.props.actorName}</div>
                    <button className="btn btn-flat teal white-text waves-effect"
                            onClick={this.toggleLikes}>Like
                    </button>
                </div>
            );
        }
        return (
            <div className="card">
                <div className="card-content">
                    <div>Likes : {this.state.count}</div>
                    {content}
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        this.getInitialLikesState();
        this.getInitialLikesCount();
    },
});
