/**
 * @propFunctions: onItemClick
 * */
let GenreOption = React.createClass({
    render: function () {
        return (
            <div className="collection-item" style={{cursor: 'pointer'}}
                 onClick={e => this.props.onItemClick(this.props.id)}>
                {this.props.name}
                <div className="secondary-content"><i
                    className="material-icons">{this.props.isSelected ? 'check' : 'check_box_outline_blank'}</i></div>
            </div>
        );
    }
});

window.SeekEntertainmentApp = React.createClass({
    isValid: function () {
        if (this.refs.text.value === '') {
            Materialize.toast('Please write something about what type of entertainment you want', 2000);
            return false;
        }
        let selectedIds = [];
        this.state.wrappedGenres.forEach(wg => {
            if (wg.isSelected) {
                selectedIds.push(wg.genre.id);
            }
        });
        if (selectedIds.length === 0) {
            Materialize.toast('Please give at least one genre of your desired entertainment', 2000);
            return false;
        }
        return true;
    },
    getInitialState: function () {
        return {
            wrappedGenres: [],
        }
    },
    getDefaultProps: function () {
        return {
            contextPath: '',
            url: '/seek/create',
            genreFetchUrl: '/genre/fetch/all',
        }
    },
    getInitialGenres: function () {
        let url = this.props.contextPath + this.props.genreFetchUrl;
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
                    let wrappedData = data.map(genre => {
                        return {isSelected: false, genre: genre}
                    });
                    self.setState({wrappedGenres: wrappedData});
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    createSeek: function () {
        // validation
        if (!this.isValid()) {
            return;
        }
        let selectedIds = [];
        this.state.wrappedGenres.forEach(wg => {
            if (wg.isSelected) {
                selectedIds.push(wg.genre.id);
            }
        });
        let self = this;
        // ajax call
        $.ajax({
            url: self.props.contextPath + self.props.url,
            type: 'POST',
            data: {
                text: self.refs.text.value,
                genreIds: JSON.stringify(selectedIds),
            },
            success: function (r) {
                let json = JSON.parse(r);
                if (json.status === -1) {
                    Materialize.toast(json.error, 2000);
                }
                else {
                    let data = json.data;
                    Materialize.toast(data, 2000);
                    // reset
                    self.setState(ps => {
                        ps.wrappedGenres.forEach(wrappedGenre => {
                            wrappedGenre.isSelected = false;
                        });
                        return {wrappedGenres: ps.wrappedGenres}
                    });
                    $(self.refs.form).find('input, textarea').val('');
                }
            },
            error: function (data) {
                Materialize.toast('Server Error', 2000);
            }
        });
    },
    toggleSelect: function (id) {
        this.setState(ps => {
            ps.wrappedGenres.forEach(wrappedGenre => {
                if (wrappedGenre.genre.id === id) {
                    wrappedGenre.isSelected = !wrappedGenre.isSelected;
                }
            });
            return {wrappedGenres: ps.wrappedGenres}
        });
    },
    render: function () {
        let genres = this.state.wrappedGenres.map(wrappedGenre => {
            return <GenreOption
                key={wrappedGenre.genre.id}
                id={wrappedGenre.genre.id}
                name={wrappedGenre.genre.name}
                isSelected={wrappedGenre.isSelected}
                onItemClick={this.toggleSelect}
            />;
        });
        return (
            <div ref="form" className="row">
                <div className="input-field col s8">
                        <textarea ref="text" placeholder="Tell something about what you want..." defaultValue=""
                                  className="materialize-textarea">
                        </textarea>
                </div>
                <div className="input-field col s4">
                    <button onClick={this.createSeek}
                            className="btn-flat btn right">
                        Seek
                    </button>
                </div>
                <div className="col s12" ref="genres">
                    <div className="collection with-header">
                        <span className="flow-text">Genres</span>
                        {genres}
                    </div>
                </div>
            </div>
        );
    },
    componentDidMount: function () {
        this.getInitialGenres();
    }
});
