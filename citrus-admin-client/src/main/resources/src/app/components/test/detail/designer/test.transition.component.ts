import {Component, Output, EventEmitter, Input} from '@angular/core';

@Component({
    selector: "test-transition",
    template: `<div class="test-transition" (mouseenter)="focused = true" (mouseleave)="focused = false">
    <span>&nbsp;</span>
    <div [hidden]="!focused || actionIndex < 0" class="add-action" (click)="add($event)">
        <i class="fa fa-plus"></i>
    </div>    
</div>`
})
export class TestTransitionComponent {

    @Input() actionIndex: number;

    @Output() added = new EventEmitter(true);

    constructor() {}

    focused = false;

    add(event: MouseEvent) {
        this.added.emit(this.actionIndex);
        event.stopPropagation();
        return false;
    }

}
