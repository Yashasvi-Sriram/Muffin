let SEAT_STATE = {
    NON_EXISTING: 0,
    BOOKED: 1,
    FREE: 2,
    BLOCK: 3,
};

/**
 * @propFunctions: freeSeat, blockSeat
 * */
let Seat = React.createClass({
    getInitialState: function () {
        return {
            seatState: SEAT_STATE.FREE,
        }
    },
    getDefaultProps: function () {
        return {
            seatState: SEAT_STATE.NON_EXISTING,
            width: 20,
            height: 30,
            margin: 10,
        }
    },
    render: function () {
        let bgColor = '';
        switch (this.state.seatState) {
            case SEAT_STATE.NON_EXISTING:
                bgColor = 'black';
                break;
            case SEAT_STATE.FREE:
                bgColor = 'white';
                break;
            case SEAT_STATE.BOOKED:
                bgColor = 'blue';
                break;
            case SEAT_STATE.BLOCK:
                bgColor = 'green';
                break;
            default:
                break;
        }
        return (
            <span className="seat"
                  title={(this.props.y + 1) + ',' + (this.props.x + 1)}
                  onClick={e => {
                      if (this.state.seatState === SEAT_STATE.FREE) {
                          this.setState((ps, pp) => {
                              return {
                                  seatState: SEAT_STATE.BLOCK,
                              }
                          });
                          this.props.blockSeat(this.props.x, this.props.y);
                      } else if (this.state.seatState === SEAT_STATE.BLOCK) {
                          this.setState((ps, pp) => {
                              return {
                                  seatState: SEAT_STATE.FREE,
                              }
                          });
                          this.props.freeSeat(this.props.x, this.props.y);
                      }
                  }}
                  style={{
                      position: 'absolute',
                      border: '2px solid black',
                      backgroundColor: bgColor,
                      left: ((this.props.width + this.props.margin) * (this.props.x + 1)) + 'px',
                      top: ((this.props.height + this.props.margin) * (this.props.y + 1)) + 'px',
                      width: this.props.width + 'px',
                      height: this.props.height + 'px',
                  }}
            >
            </span>
        );
    },
    componentDidMount: function () {
        this.setState((ps, pp) => {
            return {
                seatState: pp.seatState,
            }
        });
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
            <span className="seat pink white-text center"
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

window.BookingApp = React.createClass({
    getDefaultProps: function () {
        return {
            showId: 0,
            alreadyBookedSeats: '',
            theatreSeats: '',
            theatre: '',
        }
    },
    theatreSeatHashMap: {},
    seatsToBeBooked: {},
    submit: function () {
        let seatsToBooked = [];
        for (let key in this.seatsToBeBooked) {
            if (this.seatsToBeBooked.hasOwnProperty(key)) {
                let seat = this.props.theatreSeats[this.seatsToBeBooked[key]];
                if (seat !== undefined) {
                    seatsToBooked.push(seat);
                }
            }
        }
        if (seatsToBooked.length === 0) {
            Materialize.toast('Please select atleast one seat', 2000);
            return;
        }
        $(this.refs.seatsToBeBookedFormField).val(JSON.stringify(seatsToBooked));
        $(this.refs.form).submit();
    },
    freeSeat: function (x, y) {
        this.seatsToBeBooked[x + '-' + y] = undefined;
    },
    blockSeat: function (x, y) {
        this.seatsToBeBooked[x + '-' + y] = this.theatreSeatHashMap[x + '-' + y];
    },
    render: function () {
        let theatre = this.props.theatre;
        let alreadyBookedSeats = this.props.alreadyBookedSeats;
        let theatreSeats = this.props.theatreSeats;
        let dimX = -1;
        let dimY = -1;
        theatreSeats.forEach(seat => {
            if (seat.x > dimX) {
                dimX = seat.x;
            }
            if (seat.y > dimY) {
                dimY = seat.y;
            }
        });
        dimX++;
        dimY++;
        // dimY = rows, dimX = columns
        let seatBitmap = [];
        for (let y = 0; y < dimY; y++) {
            seatBitmap.push([]);
            for (let x = 0; x < dimX; x++) {
                seatBitmap[y].push(SEAT_STATE.NON_EXISTING);
            }
        }
        theatreSeats.forEach(seat => {
            seatBitmap[seat.y][seat.x] = SEAT_STATE.FREE;
        });
        alreadyBookedSeats.forEach(seat => {
            seatBitmap[seat.y][seat.x] = SEAT_STATE.BOOKED;
        });
        // seatBitmap is ready
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
        for (let y = 0; y < dimY; y++) {
            indexedSeatsHTML.push([]);
            indexedSeatsHTML[0].push(
                <Index x={0}
                       y={y + 1}
                       i={y + 1}
                       key={y + 'y'}/>
            );
            for (let x = 0; x < dimX; x++) {
                let seatState = seatBitmap[y][x];
                indexedSeatsHTML[y].push(
                    <Seat x={x}
                          y={y}
                          key={x + '-' + y}
                          seatState={seatState}
                          freeSeat={this.freeSeat}
                          blockSeat={this.blockSeat}
                    />
                );
            }
        }
        return (
            <div>
                <form action="/show/book" method="POST" style={{display: 'none'}} ref="form">
                    <input name="showId" defaultValue={this.props.showId}/>
                    <input name="seatsToBeBooked" ref="seatsToBeBookedFormField"/>
                </form>
                <div className="row flow-text">
                    <div className="col s3">
                        Screen No. {theatre.screenNo}
                    </div>
                    <div className="col s3">
                        Rows : {dimY}
                    </div>
                    <div className="col s3">
                        Columns : {dimX}
                    </div>
                    <div className="col s3">
                        <button onClick={this.submit} className="btn btn-flat pink white-text">
                            Book
                        </button>
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
    },
    componentDidMount: function () {
        let theatreSeats = this.props.theatreSeats;
        theatreSeats.forEach((seat, i) => {
            this.theatreSeatHashMap[seat.x + '-' + seat.y] = i;
        });
    }
});
