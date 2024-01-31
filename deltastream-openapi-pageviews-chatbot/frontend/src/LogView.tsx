import { useState } from "react";

import { Typography } from '@mui/material';

const LogView = ({sse}:{ sse: EventSource}) => {

    const [state, setState] = useState({
        logs: [],
      });

    sse.onmessage =(e) => {
      if(e.data !== "") {
        setState( (prevState) => ({ logs: [...prevState.logs, e.data] }) );
      }
    };

    let { logs } = state;
    return (
        <>
            <Typography variant="h5" component="h5" gutterBottom margin={'10px'}>Application logs</Typography>
            <ul>
              { logs.map( (log, index) => <li key={index}> {log}</li>)}
            </ul>
        </>
      );
}

export default LogView;