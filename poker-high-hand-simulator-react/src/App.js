import React, { useState } from 'react';
import './App.css';

const PokerTable = ({ tableNumber, chairsPerTable, cardsPerChair }) => {
    const angleStep = 360 / chairsPerTable;
    const radius = 220;
    const tableSize = 600;
    const chairSize = 100;

    const getChairPosition = (index) => {
        const angle = index * angleStep;
        const x = (tableSize / 3)  * Math.cos((angle * Math.PI) / 180) - (chairSize/chairsPerTable);
        const y = (tableSize / 3)  * Math.sin((angle * Math.PI) / 180) - (chairSize/chairsPerTable);
        return { transform: `translate(${x}px, ${y}px)` };
    };

    return (
        <div className="table">
            <div className="table-center">Table {tableNumber}</div>
            {Array.from({ length: chairsPerTable }).map((_, index) => (
                <div key={index} className="seat" style={getChairPosition(index)}>
                    <div className="cards">
                        {Array.from({ length: cardsPerChair }).map((_, cardIndex) => (
                            <div key={cardIndex} className="card">Card {cardIndex + 1}</div>
                        ))}
                    </div>
                    Seat {index + 1}
                </div>
            ))}
        </div>
    );
};

const HighHandBoard = ({ tableId }) => {
    const [nlhQualifiers, setNlhQualifiers] = useState('');
    const [ploQualifiers, setPloQualifiers] = useState('');

    const handleNlhChange = (e) => {
        if (e.target.value.length <= 5) {
            setNlhQualifiers(e.target.value);
        }
    };

    const handlePloChange = (e) => {
        if (e.target.value.length <= 5) {
            setPloQualifiers(e.target.value);
        }
    };

    return (
        <div className="high-hand-board">
            <h2>High Hand</h2>
            <div className="high-hand-cards">
                {Array.from({ length: 5 }).map((_, index) => (
                    <div key={index} className="high-hand-card">Card {index + 1}</div>
                ))}
            </div>
            <div className="table-id">Table ID: {tableId}</div>
            <div className="qualifiers-box">
                <div>
                    <label>NLH:</label>
                    <input type="text" value={nlhQualifiers} onChange={handleNlhChange} />
                </div>
                <div>
                    <label>PLO:</label>
                    <input type="text" value={ploQualifiers} onChange={handlePloChange} />
                </div>
            </div>
        </div>
    );
};

const App = () => {
    const [numPLOTables, setNumPLOTables] = useState(0);
    const [numNLHTables, setNumNLHTables] = useState(0);
    const [seatsPerTable, setSeatsPerTable] = useState(0);

    const handlePLOChange = (e) => setNumPLOTables(parseInt(e.target.value));
    const handleNLHChange = (e) => setNumNLHTables(parseInt(e.target.value));
    const handleSeatsChange = (e) => setSeatsPerTable(parseInt(e.target.value));

    return (
        <div>
            <h1>Poker High Hand Simulator</h1>
            <div className="top-container">
                <div className="high-hand-board">
                    <h2>High Hand</h2>
                    <div className="high-hand-cards">
                        {Array.from({length: 5}).map((_, index) => (
                            <div key={index} className="high-hand-card">Card {index + 1}</div>
                        ))}
                    </div>
                    <div className="table-id">Table ID: 1</div>
                </div>
                <div className="qualifiers-box">
                    <label>
                        NLH High Hand Minimum Qualifier:
                        <input defaultValue="22233" type="text" maxLength="5"/>
                    </label>
                    <label>
                        PLO High Hand Minimum Qualifier:
                        <input defaultValue="22233" type="text" maxLength="5"/>
                    </label>
                </div>
            </div>
            <div className="inputs-container">
                <label>
                    Number of PLO Tables:
                    <input defaultValue="2" type="number" value={numPLOTables} onChange={handlePLOChange}/>
                </label>
                <label>
                    Number of NLH Tables:
                    <input defaultValue="2" type="number" value={numNLHTables} onChange={handleNLHChange}/>
                </label>
                <label>
                    Seats Per Table:
                    <input defaultValue="8" type="number" value={seatsPerTable} onChange={handleSeatsChange}/>
                </label>
            </div>

            <div id="tables-container">
                {Array.from({length: numPLOTables}).map((_, index) => (
                    <PokerTable key={`plo-${index}`} tableNumber={index + 1} chairsPerTable={seatsPerTable}
                                cardsPerChair={4}/>
                ))}
                {Array.from({length: numNLHTables}).map((_, index) => (
                    <PokerTable key={`nlh-${index}`} tableNumber={index + 1} chairsPerTable={seatsPerTable}
                                cardsPerChair={2}/>
                ))}
            </div>
        </div>
    );
};

export default App;
