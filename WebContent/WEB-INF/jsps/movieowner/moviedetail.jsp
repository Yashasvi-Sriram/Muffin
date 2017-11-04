<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="m" tagdir="/WEB-INF/tags" %>
<m:base>
    <jsp:attribute name="title">Movie Editor</jsp:attribute>
    <jsp:attribute name="css">
        <style>
            input {
                border: none !important;
                box-shadow: none !important;
                outline: none !important;
                color: black !important;
                font-size: x-large !important;
                margin-left: 5% !important;
                width: 80% !important;
                margin-right: 5% !important;
                margin-bottom: 0 !important;
            }

            input:focus {
                border: none !important;
                box-shadow: none !important;
                outline: 1px solid black !important;
            }
        </style>
    </jsp:attribute>
    <jsp:body>
        <script type="text/babel">
            let truncate = function (string, maxLength) {
                let label;
                if (string.length > maxLength) {
                    label = string.substring(0, maxLength) + '...';
                }
                else {
                    label = string;
                }
                return label;
            };
            let isCharacterValid = function (actorName, characterName) {
                if (actorName === '') {
                    Materialize.toast('Actor Name name is empty!', 3000);
                    return false;
                }
                if (actorName.length > 50) {
                    Materialize.toast('Actor name must be less than 50 characters!', 3000);
                    return false;
                }

                if (characterName === '') {
                    Materialize.toast('Character Name name is empty!', 3000);
                    return false;
                }
                if (characterName.length > 50) {
                    Materialize.toast('Character name must be less than 50 characters!', 3000);
                    return false;
                }

                return true;
            };

            /**
             * @propFunctions: onDeleteClick
             * */
            let CharacterItem = React.createClass({
                getInitialState: function () {
                    return {
                        inReadMode: true,
                    }
                },
                render: function () {
                    return (
                            <tr title={this.props.name}>
                                <td className="flow-text">{truncate(this.props.actorName, 25)}</td>
                                <td className="flow-text">{truncate(this.props.name, 25)}</td>
                                <td>
                                </td>
                                <td>
                                    <a href="#"
                                       onClick={(e) => {
                                           this.props.onDeleteClick(this.props.id)
                                       }}
                                       className="btn-floating waves-effect waves-light red">
                                        <i className="material-icons">remove</i>
                                    </a>
                                </td>
                            </tr>
                    );
                }
            });

            /**
             * @propFunctions: onActorClick
             * */
            let ActorSearchResult = React.createClass({
                render: function () {
                    return (
                            <tr>
                                <td onClick={e => this.props.onActorClick(this.props.actorName)}>{this.props.actorName}</td>
                            </tr>
                    );
                }
            });

            let CharacterEditor = React.createClass({
                getInitialState: function () {
                    return {
                        movieId: ${requestScope.movieId},
                        characters: [
                            <jstl:forEach items="${requestScope.characterList}" var="character">
                            {
                                id: ${character.id},
                                name: '${character.name}',
                                movieId: '${character.movieId}',
                                actorId: '${character.actorId}',
                                actorName: '${character.actorName}',
                            },
                            </jstl:forEach>
                        ],
                        actors: [],
                    }
                },
                createCharacter: function () {
                    let self = this;
                    // validation
                    if (!isCharacterValid(this.refs.actorName.value, this.refs.characterName.value)) {
                        return;
                    }
                    // ajax call
                    $.ajax({
                        url: '${pageContext.request.contextPath}/character/create',
                        type: 'POST',
                        data: {
                            actorName: this.refs.actorName.value,
                            characterName: this.refs.characterName.value,
                            movieId: this.state.movieId
                        },
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            }
                            else {
                                let data = json.data;
                                self.setState((prevState, props) => {
                                    prevState.characters.push(data);
                                    return prevState;
                                });
                                $(self.refs.createCharacterForm).find('input').val('');
                                self.setState(ps => {
                                    return {actors: []};
                                });
                            }
                        },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                },
                deleteCharacter: function (id) {
                    let self = this;
                    $.ajax({
                        url: '${pageContext.request.contextPath}/character/delete',
                        type: 'GET',
                        data: {id: id},
                        success: function (r) {
                            let json = JSON.parse(r);
                            if (json.status === -1) {
                                Materialize.toast(json.error, 2000);
                            }
                            else {
                                self.setState((prevState, props) => {
                                    let delIndex = -1;
                                    prevState.characters.forEach((character, i) => {
                                        if (character.id === id) {
                                            delIndex = i;
                                        }
                                    });
                                    prevState.characters.splice(delIndex, 1);
                                    return prevState;
                                });
                            }
                        },
                        error: function (data) {
                            Materialize.toast('Server Error', 2000);
                        }
                    });
                },
                updateActor: function (text) {
                    this.refs.actorName.value = text;
                },
                onRegexInputKeyDown: function (e) {
                    let self = this;
                    switch (e.keyCode || e.which) {
                        // Enter Key
                        case 13:
                            let self = this;
                            $.ajax({
                                url: '${pageContext.request.contextPath}/actor/search',
                                type: 'GET',
                                data: {searchKey: e.target.value},
                                success: function (r) {
                                    let json = JSON.parse(r);
                                    if (json.status === -1) {
                                        Materialize.toast(json.error, 2000);
                                    }
                                    else {
                                        let data = json.data;
                                        self.setState(ps => {
                                            return {actors: data};
                                        });
                                        $(self.refs.actors).show();
                                    }
                                },
                                error: function (data) {
                                    Materialize.toast('Server Error', 2000);
                                }
                            });
                            break;
                        // Escape key
                        case 27:
                            $(this.refs.actors).hide();
                            break;
                        default:
                            break;
                    }
                },
                render: function () {
                    let actors = this.state.actors.map(actor => {
                        return <ActorSearchResult
                                key={actor.id}
                                id={actor.id}
                                actorName={actor.name}
                                onActorClick={this.updateActor}
                        />;
                    });
                    return (
                            <div>
                                <a href="${pageContext.request.contextPath}/movieowner/movieeditor">Go to Movies</a>
                                <table className="highlight centered striped">
                                    <thead>
                                    <tr>
                                        <th>Actor Name</th>
                                        <th>Character Name</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {
                                        this.state.characters.map(m => {
                                            return <CharacterItem key={m.id}
                                                                  id={m.id}
                                                                  name={m.name}
                                                                  actorName={m.actorName}
                                                                  onDeleteClick={this.deleteCharacter}

                                            />;
                                        })
                                    }
                                    <tr className="create-character-form"
                                        ref="createCharacterForm">
                                        <td>
                                            <input type="text" ref="actorName" name="actorName" placeholder="Actor Name"
                                                   defaultValue="" onKeyDown={this.onRegexInputKeyDown}/>
                                        </td>
                                        <td>
                                            <input type="text"
                                                   ref="characterName"
                                                   name="characterName"
                                                   placeholder="Character Name"
                                                   defaultValue=""/>
                                        </td>
                                        <td>
                                        </td>
                                        <td>
                                            <button onClick={this.createCharacter}
                                                    className="btn-floating waves-effect waves-light green">
                                                <i className="material-icons">add</i>
                                            </button>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                                <table ref="actors" className="striped highlight">
                                    <tbody>
                                    {actors.length === 0 ? <tr>
                                        <td className="red white-text">No matching actors</td>
                                    </tr> : actors}
                                    </tbody>
                                </table>
                            </div>
                    );
                }
            });

            ReactDOM.render(<CharacterEditor/>, document.getElementById('app'));
        </script>
        <div id="app" class="container"></div>
    </jsp:body>
</m:base>