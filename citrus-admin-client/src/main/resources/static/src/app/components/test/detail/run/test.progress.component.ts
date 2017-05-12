import {Component,  Input} from '@angular/core';

@Component({
    selector: "test-progress",
    styles: [`
        .progress-bar {
            transition: color .5s,  width .25s;
        }
    `],
    template: `      
    <div class="progress">
      <div  [class]="progressBarCssClass"
            role="progressbar" 
            [attr.aria-valuenow]="completed" 
            aria-valuemin="0" 
            aria-valuemax="100" 
            [style.width.%]="completed" 
            >{{message}}</div>
    </div>`
})
export class TestProgressComponent {

    @Input() completed: number;
    @Input() failed: boolean;

    get progressBarCssClass() {
        return this.failed ? 'progress-bar progress-bar-danger' : this.completed == 100 ? 'progress-bar progress-bar-success' : 'progress-bar progress-bar-primary';
    }

    get message() {
        return this.failed ? 'Failed' : `${this.completed}%`
    }
}