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
import { TestComponent } from './components/test/test.component';
import { HttpClientModule } from '@angular/common/http';
import { DatabaseHeaderComponent } from './components/database-header/database-header.component';
import { LogoHeaderComponent } from './components/logo-header/logo-header.component';
import { SqlEntryRowComponent } from './components/sql-entry-row/sql-entry-row.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {MatSnackBarModule} from '@angular/material/snack-bar'; 
import {MatSelectModule, MatAutocompleteModule, MatFormFieldModule, MatInputModule, MatCheckboxModule } from '@angular/material';

@NgModule({
  declarations: [
    AppComponent,
    DropdownAutocompleteComponent,
    DatabaseHeaderComponent,
    TestComponent,
    LogoHeaderComponent,
    SqlEntryRowComponent
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
    MatCheckboxModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { 

  

}
