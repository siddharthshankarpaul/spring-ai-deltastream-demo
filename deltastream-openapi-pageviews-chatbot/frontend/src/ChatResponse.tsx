import React, { useEffect, useState } from 'react';
import { Loading } from 'react-simple-chatbot';
import ChatResponseProps from './types/ChatResponse.type';

const ChatResponse: React.FC<ChatResponseProps> = ({ steps, triggerNextStep }) => {
  const [loading, setLoading] = useState(true);
  const [result, setResult] = useState('');
  const [, setTrigger] = useState(false);

  const triggetNext = () => {
    setTrigger(true);
    if(triggerNextStep)
     triggerNextStep();
  };

  useEffect(() => {
    const search = steps?.search.value;

    const queryUrl = 'http://localhost:8080/chat/text';

    const xhr = new XMLHttpRequest();

    const readyStateChange = () => {
      if (xhr.readyState === 4) {
        const data = (xhr.responseText);
        if (data && data.length > 0) {
          setLoading(false);
          setResult(data);
          triggetNext();
        } else {
          setLoading(false);
          setResult('Not found.');
        }
      }
    };

    xhr.addEventListener('readystatechange', readyStateChange);

    xhr.open('POST', queryUrl);

    xhr.setRequestHeader('Content-Type', 'application/json;charset=UTF-8');
    xhr.send(JSON.stringify({ query: search }));

    return () => {
      xhr.removeEventListener('readystatechange', readyStateChange);
    };
  }, []);

  return (
    <div className="dbpedia">
      {loading ? <Loading /> : result}
    </div>
  );
};

export default ChatResponse;


