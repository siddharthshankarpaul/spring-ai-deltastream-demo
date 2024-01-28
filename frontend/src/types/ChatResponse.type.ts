export default interface ChatResponseProps {
    steps?: { search: { value: string } };
    triggerNextStep?: () => void;
  }