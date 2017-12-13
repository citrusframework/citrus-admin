import {NgModule} from "@angular/core";
import {AutoCompleteComponent} from "./autocomplete";
import {DialogComponent} from "./dialog";
import {PillComponent, PillsComponent} from "./pills";
import {SidebarMenuComponent, MenuItemComponent} from "./sidebar";
import {TabsComponent, TabComponent} from "./tabs";
import {FormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {DetailPanelComponent, DetailPanelHeadingComponent, DetailPanelBodyComponent} from "./panel.component";
import {OutletComponent} from "./outlet.component";
import {RouterModule} from "@angular/router";
import {FormGroupComponent, InputWithAddonComponent} from "./forms.component";
import {AlertConsole} from './alert.console';
import {AlertDialog} from './alert.dialog';
import {EndpointLabelComponent} from "./endpoint-label.component";

const components = [
    AutoCompleteComponent,
    DialogComponent,
    PillComponent,
    PillsComponent,
    SidebarMenuComponent,
    TabsComponent,
    TabComponent,
    MenuItemComponent,
    DetailPanelComponent,
    DetailPanelHeadingComponent,
    DetailPanelBodyComponent,
    OutletComponent,
    FormGroupComponent,
    AlertConsole,
    AlertDialog,
    InputWithAddonComponent,
    EndpointLabelComponent
];

@NgModule({
    imports: [
        FormsModule,
        CommonModule,
        RouterModule
    ],
    declarations: components,
    exports: components,

})
export class UtilComponentsModule {}
