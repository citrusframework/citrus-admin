import { Component, Input, Output, EventEmitter, ElementRef } from 'angular2/core';

declare var jQuery:any;

@Component({
    selector: 'div.dialog]',
    host: {
        '(document:keyup)': 'handleKeyUp($event)',
    },
    template: `<div id="{{name}}" class="modal fade">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h3 class="modal-title">{{title}}</h3>
      </div>
      <div class="modal-body" style="min-height: 200px;">
        <ng-content></ng-content>
      </div>
      <div class="modal-footer">
        <button *ngIf="showConfirm == 'yes'" (click)="confirm()" type="button" class="btn btn-primary">Ok</button>
        <button *ngIf="showClose == 'yes'" (click)="close()" type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>`
})
export class Dialog {
    @Output() confirmed = new EventEmitter(true);
    @Output() closed = new EventEmitter(true);

    @Input("dialog-id") name: string;
    @Input("dialog-title") title: string = "Dialog";
    @Input("dialog-close") showClose: string = "yes";
    @Input("dialog-confirm") showConfirm: string = "yes";

    open() {
        jQuery('#' + this.name).modal();
    }

    close() {
        this.closed.emit(this.name);
        jQuery('#' + this.name).modal('hide');
    }

    confirm() {
        this.confirmed.emit(this.name);
        this.close();
    }

    handleKeyUp(event) {
        if (event && event.key == "Escape") {
            this.close();
        }

        if (event && event.key == "C") {
            this.close();
        }
    }
}