import {useState} from "react";
import {useSubscription} from "react-stomp-hooks";
import {Base64Codec as sc} from "nats/lib/nats-base-client/base64";

export type SubscribingComponentProps = {
    topic: string;
}
const SubscribingComponent = ({topic}: SubscribingComponentProps) => {
    const [lastMessage, setLastMessage] = useState("No message received yet");

    //Subscribe to /topic/test, and use handler for all received messages
    //Note that all subscriptions made through the library are automatically removed when their owning component gets unmounted.
    //If the STOMP connection itself is lost they are however restored on reconnect.
    //You can also supply an array as the first parameter, which will subscribe to all destinations in the array
    useSubscription(topic, (message:any) => setLastMessage("" + sc.decode(message.body)));


    return (<div>Last Message: {lastMessage}</div>);
};


export default SubscribingComponent;
