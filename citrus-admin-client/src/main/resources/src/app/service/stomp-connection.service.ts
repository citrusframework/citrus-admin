import {Injectable} from "@angular/core";
import * as Stomp from 'stompjs';
import * as SockJS from "sockjs-client";
import {Subject} from "rxjs/Subject";
import {Observable} from "rxjs/Observable";
import {Observer} from "rxjs/Observer";
import * as _ from 'lodash'

export enum ConnectionStatus {
    NOT_CONNECTED,
    WAITING,
    CONNECTED
}

export class Topic extends Subject<Stomp.Message> {
    constructor(
        private connection:Stomp.Client,
        private topic:string
    ) {
        super();
        connection.subscribe(topic, m => this.next(m))
    }
}

export class StompConnection {
    private debug: boolean = true;
    private stompClient: Stomp.Client;
    private topics: Map<string, Topic> = new Map();
    private connectionObserver: Observable<StompConnection>;
    private connectionStatus:ConnectionStatus = ConnectionStatus.NOT_CONNECTED;

    constructor(private url: string) {
        this.stompClient = Stomp.over(new SockJS(this.url) as any);

        this.stompClient.debug = _.wrap(this.stompClient.debug, (fn: (...args: string[]) => any, ...args: string[]) => {
            if (this.debug) {
                return fn.apply(this.stompClient, args);
            } else {
                return null;
            }
        }) as (...args: string[]) => any;

    }

    connect() {
        if (!this.connectionObserver) {
            this.connectionObserver = Observable.create((observer: Observer<StompConnection>) => {
                if(this.connectionStatus === ConnectionStatus.NOT_CONNECTED) {
                    this.connectionStatus = ConnectionStatus.WAITING;
                    this.stompClient.connect({} as any,
                        (frame: Stomp.Frame) => {
                            if (frame) {
                                observer.next(this);
                                this.connectionStatus = ConnectionStatus.CONNECTED;
                            }
                            return () => {
                                this.stompClient.disconnect(() => {})
                            }
                        },
                        (error: any) => observer.error(error)
                    );
                }
                if(this.connectionStatus === ConnectionStatus.CONNECTED) {
                    observer.next(this);
                }
            });
        }
        return this.connectionObserver;
    }

    subscribeToTopic(topic: string):Topic {
        if (!this.topics.has(topic)) {
            this.topics.set(topic, new Topic(this.stompClient, topic))
        }
        return this.topics.get(topic);
    }
}

@Injectable()
export class StompConnectionService {

    connections: Map<string, StompConnection> = new Map();

    constructor() {
    }

    getConnection(url: string) {
        if (!this.connections.has(url)) {
            this.connections.set(url, new StompConnection(url))
        }
        return this.connections.get(url);
    }
}