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
        }
    },
    addMovieReview: function () {
        let self = this;
        // validation
        if (!isReviewValid(this.refs.movieName.value, this.refs.movieRating.value, this.refs.movieReview.value)) {
            return;
        }
        // ajax call
        console.log(this.refs.movieRating.value);

        $.ajax({
            url: '/review/create',
            type: 'POST',
            data: {
                movieName: this.refs.movieName.value,
                movieRating: this.refs.movieRating.value,
                movieReview: this.refs.movieReview.value
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
                <input type="text" ref="movieName"
                       name="movieName" placeholder="Movie Name" defaultValue=""/>
                <input type="number" min="1" max="10"
                       ref="movieRating" name="movieRating" placeholder="Movie Rating"
                       defaultValue=""/>
                <input type="text" ref="movieReview"
                       name="movieReview" placeholder="Movie Review" defaultValue=""/>
                <button onClick={this.addMovieReview}
                        className="btn-floating waves-effect waves-light green">
                    <i className="material-icons">add</i>
                </button>
            </div>
        );
    }
});
