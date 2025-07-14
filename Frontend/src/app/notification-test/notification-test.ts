import { Component, signal } from '@angular/core';
import * as Stomp from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs.js';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-notification-test',
  standalone: true,
  imports: [NgFor],
  templateUrl: './notification-test.html',
  styleUrl: './notification-test.css'
})
export class NotificationTest {
  public notifications = signal<string[]>([]);
  private stompClient: Stomp.Client | null = null;

  constructor() {
    this.connect();
  }

  connect() {
    const socket = new SockJS('http://localhost:8099/ws');
    this.stompClient = new Stomp.Client({
      webSocketFactory: () => socket as any,
      debug: (str) => console.log(str),
      reconnectDelay: 5000,
      onConnect: () => {
        this.stompClient?.subscribe('/topic/public-noti', (message) => {
          if (message.body) {
            const msgObj = JSON.parse(message.body);
            console.log('Received notification:', msgObj.messageContent); // <-- Add this line
            this.notifications.update((arr) => [...arr, msgObj.messageContent]);
          }
        });
      },
      onStompError: (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
      }
    });
    this.stompClient.activate();
  }
}
