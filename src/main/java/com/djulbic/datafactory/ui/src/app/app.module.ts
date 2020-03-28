import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';

import { Component } from '@angular/core';
import {map, startWith} from 'rxjs/operators';
import {combineLatest, Observable, of, from} from 'rxjs';
import {FormControl} from '@angular/forms';
import { State,states } from './model/State';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import { DropdownAutocompleteComponent } from './components/dropdown-autocomplete/dropdown-autocomplete.component';
import { HttpClientModule } from '@angular/common/http';
import { DatabaseHeaderComponent } from './components/database-header/database-header.component';
import { SqlEntryRowComponent } from './components/sql-entry-row/sql-entry-row.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatSelectModule, MatAutocompleteModule, MatFormFieldModule, MatInputModule, MatCheckboxModule, MatDialogModule } from '@angular/material';
import { ModalDbConnectionComponent } from './components/modal/modal-db-connection/modal-db-connection.component';
import {MatIconModule} from '@angular/material/icon';
import { ModalExecutePresetComponent } from './components/modal/modal-execute-preset/modal-execute-preset.component'
@NgModule({
  declarations: [
    AppComponent,
    DropdownAutocompleteComponent,
    DatabaseHeaderComponent,
    SqlEntryRowComponent,
    ModalDbConnectionComponent,
    ModalExecutePresetComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    MatSnackBarModule,
    MatSelectModule,
    MatAutocompleteModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatDialogModule,
    MatIconModule
  ],
  entryComponents:[ ModalDbConnectionComponent, ModalExecutePresetComponent ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {



}
