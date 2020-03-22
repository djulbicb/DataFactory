import { Component, OnInit, Output, Input, EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from 'src/app/service/api-service.service';
import { DatabaseRequestConfig } from 'src/app/model/DatabaseRequestConfig';
import { ColumnSql } from 'src/app/model/ColumnSql';
import { MatSnackBar } from '@angular/material';

@Component({
  selector: 'database-header',
  templateUrl: './database-header.component.html',
  styleUrls: ['./database-header.component.css']
})
export class DatabaseHeaderComponent implements OnInit {

  @ViewChild("inputInsertCount", {static:null}) inputInsertCount:ElementRef;

  dataLibraryLanguages:Array<string>;

  inputdatabases:Observable<any>;
  databases:Array<any>;
  selectedDatabase:string = "";
  tables:Array<any>;
  selectedTable:string = "";
  inputColumns:Array<ColumnSql>;

  autoExecute:boolean = true;

  @Output() emmitDatabaseChanged = new EventEmitter<String>();
  @Output() emmitShowColumns = new EventEmitter<DatabaseRequestConfig>();
  @Output() emmitExecute = new EventEmitter<String>();

  buttonBatchValues = [1, 5, 10, 25, 50, 100, 250, 500, 1000];

  constructor(private apiService:ApiService, private _snackBar: MatSnackBar) { }

  ngOnInit() {
    this.inputdatabases = this.apiService.getDatabases();

    this.apiService.getDataLibraryLanguages().subscribe((data)=>{
      this.dataLibraryLanguages = data;
    });
    this.apiService.getDatabases().subscribe((data)=>{
      this.databases = data;
    })
  }

  public getDatabaseRequestConfig(){
    let config:DatabaseRequestConfig = {
      databaseName: "",
      databaseTable: "",
      driver: "",
      url: "",
      username: "",
      password: ""
    };

    config.databaseName = this.selectedDatabase;
    config.databaseTable = this.selectedTable;
    return config;
  }

  checkAndExecute(numberOfQueries){
    this.inputInsertCount.nativeElement.value = numberOfQueries;
    if(this.autoExecute){
      this.emmitExecute.emit(numberOfQueries);
    } else{
      // this._snackBar.open("Info: Auto execute is turned off.", null, {duration: 3600, panelClass:['info-snackbar']});
    }
  }

  execute(numberOfQueries){
    this.emmitExecute.emit(numberOfQueries);
  }

  createConnection(){

  }

  onDatabaseChange(){
    let config = this.getDatabaseRequestConfig();
    this.selectedTable="";
    this.emmitDatabaseChanged.emit(this.selectedDatabase);

    this.apiService.getTables(config).subscribe((data)=>{
      this.tables = data;
      console.log(data);
    })
  }

  onTableChange(){
    let config = this.getDatabaseRequestConfig();
    this.apiService.getColumns(config).subscribe((data)=>{
      console.log(data);
      this.emmitShowColumns.emit(config);
    })
  }


}
