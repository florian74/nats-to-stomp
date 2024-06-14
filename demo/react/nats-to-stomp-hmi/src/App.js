import './App.css';
import {useEffect, useState} from "react";
import {connect} from "nats.ws";
import {StompSessionProvider} from "react-stomp-hooks";
import SubscribingComponent from "./SubscribingComponent";
import {Base64Codec as sc} from "nats/lib/nats-base-client/base64";

function App() {

  const [isConnected, setConnected] = useState(false)
  const [connection, setConnection] = useState(undefined);
  
  const [natsMessage, setNatsMessage] = useState("")
  const [natsQueue,  setNatsQueue] = useState("")
  const [stompQueue,  setStompQueue] = useState("")


    useEffect(() => {
        
        async function natsConnect() {
            const nc = await connect({servers: "ws://localhost:8443"})
            setConnection(nc)
            setConnected(nc !== undefined)
        }
        natsConnect()
        
    }, []);
  
  
  return (
    <div className="App">
        <header className="App-header">
            
            <div className={"App-panel"}>
                <h1>Nats options</h1>
                <input type={"text"} onChange={(e) => setNatsMessage(e.target.value)} placeholder={"message"}/>
                <input type={"text"} onChange={(e) => setNatsQueue(e.target.value)} placeholder={"nats queue"}/>
                <button onClick={() => {
                    if (isConnected && natsQueue !== "" && natsMessage !== "") {
                        connection.publish(natsQueue, sc.encode(natsMessage));
                        console.log("published", natsQueue, natsMessage)
                    }
                }} disabled={!isConnected || natsQueue === "" || natsMessage === ""}>Send
                </button>
            </div>
            
            
            
                
            <div className={"App-panel"}>    
                <h1>Stomp</h1>
                <input type={"text"} onChange={(e) => setStompQueue(e.target.value)} placeholder={"stomp topic"}/>
                <StompSessionProvider
                    url={"http://localhost:8667/api/live"}
                    //All options supported by @stomp/stompjs can be used here
                >
                    <SubscribingComponent topic={stompQueue}/>
                </StompSessionProvider>

            </div>
          
        </header>
    </div>
  );
}


export default App;




