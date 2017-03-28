import {Component, Input, Output, EventEmitter} from "@angular/core";
import {Directory} from "./Directory";
@Component({
    selector: 'directory',
    template: `
        <li [class]="cssClass">
            <a [rel]="directory.path">
                <ng-content></ng-content>
            </a>
            <ul *ngIf="directory.children.length">
                <directory 
                    [directory]="child" 
                    *ngFor="let child of directory.children"                     
                >{{child.name}}
                </directory>
            </ul>
        </li>
    `
})
export class DirectoryComponent {
    @Input()
    directory:Directory;

    @Output()
    toggle = new EventEmitter<Directory>();

    get cssClass() {
        return ['directory', this.directory.isOpen ? 'expanded' : 'collapsed']
    }

}