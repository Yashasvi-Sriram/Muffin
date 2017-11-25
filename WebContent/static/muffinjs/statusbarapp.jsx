window.StatusBarApp = React.createClass({
    getInitialState: function () {
        return {
            noFollowers: 0,
            noApprovals: 0,
        }
    },
    getDefaultProps: function () {
        return {
            contextPath: '',
            noApprovalsUrl: '/muff/fetch/noapprovals',
            noFollowersUrl: '/muff/fetch/nofollowers',
        }
    },
    fetchNoApprovals: function () {
        let url = this.props.contextPath + this.props.noApprovalsUrl;
        let self = this;
        $.ajax({
            url: url,
            type: 'GET',
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    if (self.state.noApprovals !== data) {
                        $(self.refs.status).fadeOut(0).fadeIn(1000);
                        self.setState({noApprovals: data});
                    }
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    fetchNoFollowers: function () {
        let url = this.props.contextPath + this.props.noFollowersUrl;
        let self = this;
        $.ajax({
            url: url,
            type: 'GET',
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    if (self.state.noFollowers !== data) {
                        $(self.refs.status).fadeOut(0).fadeIn(1000);
                        self.setState({noFollowers: data});
                    }
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    render: function () {
        return (
            <div className="card red waves-effect" ref="status" style={{cursor: 'pointer'}}>
                <div className="card-content">
                    <div title="Number of followers" className="flow-text">
                        <i className="material-icons">people</i> = {this.state.noFollowers}</div>
                    <div title="Number of approvals" className="flow-text">
                        <i className="material-icons">done</i> = {this.state.noApprovals}</div>
                    <div title="Level" className="flow-text">
                        <i className="material-icons">flash_on</i> = {Math.floor(this.state.noApprovals / 10)}</div>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        this.fetchNoApprovals();
        this.fetchNoFollowers();
        setInterval(() => {
            this.fetchNoApprovals();
            this.fetchNoFollowers();
        }, 60000); // ms
    },
});
