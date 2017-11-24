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

window.TheatreViewerApp = React.createClass({
    render: function () {
        let seatArray = this.props.seats;
        let dimX = -1;
        let dimY = -1;
        seatArray.forEach(seat => {
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
                seatBitmap[y].push(false);
            }
        }
        seatArray.forEach(seat => {
            seatBitmap[seat.y][seat.x] = true;
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
                let seat = seatBitmap[y][x];
                indexedSeatsHTML[y].push(
                    <Seat x={x}
                          y={y}
                          key={x + '-' + y}
                          exists={seat}/>
                );
            }
        }
        return (
            <div>
                <div className="row flow-text">
                    <div className="col s3">
                        Screen No. {this.props.screenNo}
                    </div>
                    <div className="col s3">
                        Rows : {dimY}
                    </div>
                    <div className="col s3">
                        Columns : {dimX}
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
