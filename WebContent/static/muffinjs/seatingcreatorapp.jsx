/**
 * ===================================================================================================================
 * =                   SCREEN SCREEN SCREEN SCREEN SCREEN SCREEN SCREEN SCREEN SCREEN SCREEN
 * =
 * =        +-------------------------------------------------------------------------------------> x
 * =        -
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -   o   o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o  o
 * =        -
 * =        y
 * */

/**
 * @propFunctions: @onSeatClick
 * */
let Seat = React.createClass({
    getDefaultProps: function () {
        return {
            exists: true,
            x: 0,
            y: 0,
            width: 20,
            height: 30,
            margin: 10,
        }
    },
    render: function () {
        return (
            <span className="seat"
                  title={(this.props.y + 1) + ',' + (this.props.x + 1)}
                  style={{
                      position: 'absolute',
                      border: '2px solid black',
                      backgroundColor: this.props.exists ? 'white' : 'black',
                      left: ((this.props.width + this.props.margin) * (this.props.x + 1)) + 'px',
                      top: ((this.props.height + this.props.margin) * (this.props.y + 1)) + 'px',
                      width: this.props.width + 'px',
                      height: this.props.height + 'px',
                  }}
                  onClick={e => {
                      this.props.onSeatClick(this.props.x, this.props.y, this.props.exists);
                  }}
            >
            </span>
        );
    }
});

let Index = React.createClass({
    getDefaultProps: function () {
        return {
            x: 0,
            y: 0,
            i: 0,
            width: 20,
            height: 30,
            margin: 10,
        }
    },
    render: function () {
        return (
            <span className="seat pink white-text center "
                  style={{
                      position: 'absolute',
                      left: ((this.props.width + this.props.margin) * this.props.x) + 'px',
                      top: ((this.props.height + this.props.margin) * this.props.y) + 'px',
                      width: this.props.width + 'px',
                      height: this.props.height + 'px',
                  }}
            >
                {this.props.i}
            </span>
        );
    }
});

window.SeatingCreatorApp = React.createClass({
    getDefaultSeatMatrix: function (dimX, dimY) {
        let seats = [];
        for (let y = 0; y < dimY; y++) {
            seats.push([]);
            for (let x = 0; x < dimX; x++) {
                seats[y].push(true);
            }
        }
        return seats;
    },
    getInitialState: function () {
        return {
            seats: this.getDefaultSeatMatrix(30, 19),
        }
    },
    getDefaultProps: function () {
        return {
            MAX_X: 50,
            MAX_Y: 50,
        }
    },
    isDimensionValid: function (dimX, dimY) {
        if (isNaN(dimY) || isNaN(dimX)
            || dimX < 1 || dimX > this.props.MAX_X
            || dimY < 1 || dimY > this.props.MAX_Y
        ) {
            Materialize.toast('Invalid Dimension', 1000);
            return false;
        }
        return true;
    },
    onDimensionChange: function () {
        let dimX = Number(this.refs.dimX.value);
        let dimY = Number(this.refs.dimY.value);
        if (this.isDimensionValid(dimX, dimY)) {
            this.setState({
                seats: this.getDefaultSeatMatrix(dimX, dimY),
            });
        }
    },
    toggleSeatExistence: function (x, y, doesExist) {
        this.setState(ps => {
            ps.seats[y][x] = !doesExist;
            return {seats: ps.seats}
        });
    },
    render: function () {
        let dimX = this.state.seats[0].length;
        let dimY = this.state.seats.length;
        let indexedSeatsHTML = [];
        indexedSeatsHTML.push([]);
        indexedSeatsHTML[0].push('+');
        for (let x = 0; x < dimX; x++) {
            indexedSeatsHTML[0].push(
                <Index x={x + 1}
                       y={0}
                       i={x + 1}
                       key={x + 'x'}/>
            );
        }
        for (let y = 0; y < this.state.seats.length; y++) {
            let ithRow = this.state.seats[y];
            indexedSeatsHTML.push([]);
            indexedSeatsHTML[0].push(
                <Index x={0}
                       y={y + 1}
                       i={y + 1}
                       key={y + 'y'}/>
            );
            for (let x = 0; x < ithRow.length; x++) {
                let seat = this.state.seats[y][x];
                indexedSeatsHTML[y].push(
                    <Seat x={x}
                          y={y}
                          key={x + '-' + y}
                          exists={seat}
                          onSeatClick={this.toggleSeatExistence}/>
                );
            }
        }
        return (
            <div>
                <div className="row">
                    <div className="input-field col s4">
                        <input placeholder="rows" type="number" ref="dimY"
                               onChange={this.onDimensionChange}
                               defaultValue={dimY}/>
                    </div>
                    <div className="input-field col s4">
                        <input placeholder="columns" type="number" ref="dimX"
                               onChange={this.onDimensionChange}
                               defaultValue={dimX}/>
                    </div>
                </div>
                <div className="screen flow-text center-align grey white-text" style={{margin: '10px'}}>
                    SCREEN
                </div>
                <div id="seat-layout" style={{position: 'relative', margin: '10px'}}>
                    {indexedSeatsHTML}
                </div>
            </div>
        );
    }
});
