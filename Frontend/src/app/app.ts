import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NotificationTest } from './notification-test/notification-test';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NotificationTest],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('Frontend');
}
