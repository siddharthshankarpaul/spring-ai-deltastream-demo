// @ts-ignore
import ChatBot from 'react-simple-chatbot';
import ChatResponse from './ChatResponse';

 
  const allSteps = [
    {
      id: '1',
      message: 'Welcome to DeltaStream chatbot!',
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
        <ChatBot steps={allSteps} width='auto' style={{height: '85vh'}} contentStyle={{height:'70vh'}} headerTitle="DeltaStream Bot"/>
    );
}


export default Chatbot;