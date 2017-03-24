import {NgModule, forwardRef} from "@angular/core";
import {AutoCompleteComponent} from "./autocomplete";
import {DialogComponent} from "./dialog";
import {PillComponent, PillsComponent} from "./pills";
import {SidebarMenuComponent, MenuItemComponent} from "./sidebar";
import {TabsComponent, TabComponent} from "./tabs";
import {FormsModule, NG_VALUE_ACCESSOR} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {DetailPanelComponent, DetailPanelHeadingComponent, DetailPanelBodyComponent} from "./panel.component";
import {OutletComponent} from "./outlet.component";
import {RouterModule} from "@angular/router";

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
    OutletComponent
]

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