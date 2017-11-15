let GenreItem = React.createClass({
    render: function () {
        return (

            <div>{this.props.name}</div>

        );
    }
});

window.MovieStatsApp = React.createClass({

    getInitialState: function () {
        return {
            genres: [],
        }
    },
    getDefaultProps: function () {
        return {
            movieId: 0,
            limit: 10,
            contextPath: '',
            statsFetchUrl: '/movie/stats',
        }
    },

    getMovieStats: function () {
        let url = this.props.contextPath + this.props.statsFetchUrl;
        let self = this;
        $.ajax({
            url: url,
            type: 'GET',
            data: {movieId: this.props.movieId},
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let stats = json.data;
                    console.log(typeof stats.genres);

                    self.setState(ps => {

                        return {userCount: stats.userCount, averageRating: stats.averageRating, genres: stats.genres};
                    });

                    console.log(self.state.userCount);
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },

    render: function () {
        let genreList = this.state.genres.map(c => {
            return <GenreItem key={c.id}
                              id={c.id}
                              name={c.name}
            />;
        });
        return (
            <div>
                <div>{genreList}</div>
                <div>Average Rating {this.state.averageRating} from {this.state.userCount} users</div>
            </div>

        );
    },
    componentDidMount: function () {
        this.getMovieStats();
    },
});
