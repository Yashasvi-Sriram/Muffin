let isReviewValid = function (name, rating, review) {
    if (name === '') {
        Materialize.toast('Movie name is empty!', 3000);
        return false;
    }
    if (name.length > 50) {
        Materialize.toast('Movie name must be less than 50 characters!', 3000);
        return false;
    }

    if (rating === '' && review === '') {
        Materialize.toast('Both rating and review cannot be empty!', 3000);
        return false;
    }


    if (isNaN(Number(rating)) && rating !== '') {
        Materialize.toast('Invalid Rating!', 3000);
        return false;
    }
    rating = Number(rating);
    if (rating < 0 || rating > 10) {
        Materialize.toast('Invalid rating!', 3000);
        return false;
    }
    return true;
};

window.GiveReviewApp = React.createClass({
    getDefaultProps: function () {
        return {
            contextPath: '',
            url: '/review/create',
        }
    },
    addMovieReview: function () {
        // validation
        if (!isReviewValid(this.refs.name.value, this.refs.rating.value, this.refs.textReview.value)) {
            return;
        }
        let self = this;
        // ajax call
        $.ajax({
            url: self.props.contextPath + self.props.url,
            type: 'POST',
            data: {
                name: this.refs.name.value,
                rating: this.refs.rating.value,
                textReview: this.refs.textReview.value
            },
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    Materialize.toast("Review Added Successfully", 2000);
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    render: function () {
        return (
            <div>
                <div className="row">
                    <div className="input-field col s12">
                        <p className="range-field">
                            <span>Rating</span>
                            <input type="range" min="0" max="10" ref="rating" step="0.1"
                                   defaultValue="0"/>
                        </p>
                    </div>
                    <div className="input-field col s12">
                        <textarea ref="textReview" placeholder="Your comment" defaultValue=""
                                  className="materialize-textarea">
                        </textarea>
                    </div>
                    <div className="input-field col s6">
                        <input type="text" ref="name" placeholder="Name of the movie" defaultValue=""/>
                    </div>
                    <div className="input-field col s6">
                        <button onClick={this.addMovieReview}
                                className="btn-flat btn">
                            <i className="material-icons">send</i>
                        </button>
                    </div>
                </div>
            </div>
        );
    }
});
