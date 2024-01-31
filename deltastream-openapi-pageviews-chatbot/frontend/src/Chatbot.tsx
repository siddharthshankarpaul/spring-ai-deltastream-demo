// @ts-ignore
import ChatBot from 'react-simple-chatbot';
import ChatResponse from './ChatResponse';
import { ThemeProvider } from 'styled-components';

const theme = {
  background: '#f5f8fb',
  headerBgColor: '#8769b9',
  headerFontColor: '#fff',
  headerFontSize: '15px',
  botBubbleColor: '#cce9f9',
  botFontColor: '#000',
  userBubbleColor: '#f2cea5',
  userFontColor: '#4a4a4a',
};

 
  const allSteps = [
    {
      id: '1',
      message: 'Welcome to GTM bot!',
      trigger: 'search',
    },
    {
      id: 'search',
      user: true,
      trigger: '3',
    },
    {
      id: '3',
      component: <ChatResponse />,
      waitAction: true,
      trigger: '4',
    },
    {
        id: '4',
        message: 'Ask Again',
        trigger: 'search',
      },
  ];
function Chatbot() {
    return (
        <ThemeProvider theme={theme}>
            <ChatBot steps={allSteps} 
            style={{height: '85vh',width: 'auto', boxShadow: '0px 0px 0px 0px'}} 
            contentStyle={{height:'70vh'}} 
            customStyle={{ borderRadius:'18px', background: '#cce9f9'}}
            headerTitle="GTM Bot"/>
        </ThemeProvider>
    );
}


export default Chatbot;