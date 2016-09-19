import { Component, Input, Output, EventEmitter, ElementRef } from 'angular2/core';

declare var jQuery:any;

@Component({
    selector: 'div.dialog]',
    template: `<div id="{{name}}" class="modal fade">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h3 class="modal-title">{{title}}</h3>
      </div>
      <div class="modal-body">
        <ng-content></ng-content>
      </div>
      <div class="modal-footer">
        <button (click)="confirm()" type="button" class="btn btn-primary">Ok</button>
        <button (click)="close()" *ngIf="showClose" type="button" class="btn btn-default" data-dismiss="modal">Close</button>
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
    @Input("show-close") showClose: boolean = true;

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
}