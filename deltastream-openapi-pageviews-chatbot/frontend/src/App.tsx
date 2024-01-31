
import Paper from '@mui/material/Paper';
import Grid from '@mui/material/Grid';
import { Box, CircularProgress } from '@mui/material';
import Chatbot from './Chatbot';
import DeltaStreamView from './DeltaStreamView';
import LogView from './LogView';
import { useEffect, useState } from 'react';

const sse: EventSource = new EventSource('http://localhost:8080/stream-log');

function App() {

  const [loading, setLoading] = useState(true);
  useEffect(() => {
    sse.onopen = () => {
      console.log('SSE open');
      setLoading(false);
    };

    sse.onerror = () => sse.close();

    return () => {
      sse.close();
    };
  }, []); 

  return (
    <>
    { loading ? <CircularProgress /> : 
    <Grid container spacing={1} style={{background: '#f3f3f3'}}>
      <Grid item xs={4}>
        <Box
          sx={{
            display: 'flex',
            flexWrap: 'wrap',
            '& > :not(style)': {
              m: 1,
              width: '100vh',
              height: '90vh',
            },
          }}
        >
          <Paper elevation={2} style={{ overflow: 'auto'}}> <DeltaStreamView/> </Paper>
        </Box>
      </Grid>

      <Grid item xs={4}>
        <Box
          sx={{
            display: 'flex',
            flexWrap: 'wrap',
            '& > :not(style)': {
              m: 1,
              width: '100vh',
              height: '90vh',
            },
          }}
        >
          <Paper elevation={2} > <LogView sse={sse}/> </Paper>

        </Box>
      </Grid>
      <Grid item xs={4}>
        <Box
          sx={{
            display: 'flex',
            flexWrap: 'wrap',
            '& > :not(style)': {
              m: 1,
              width: '100vh',
              height: '90vh',
            },
          }}
        >
          <Paper elevation={2}> <Box sx={{margin:'5px'}}><Chatbot /></Box></Paper>
        </Box>
      </Grid>
    </Grid>
    }
    </>
  );
}

export default App;