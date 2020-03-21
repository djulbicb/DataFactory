import { Component, OnInit, Output, Input, EventEmitter } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from 'src/app/service/api-service.service';
import { DatabaseRequestConfig } from 'src/app/model/DatabaseRequestConfig';
import { ColumnSql } from 'src/app/model/ColumnSql';

@Component({
  selector: 'database-header',
  templateUrl: './database-header.component.html',
  styleUrls: ['./database-header.component.css']
})
export class DatabaseHeaderComponent implements OnInit {

  dataLibraryLanguages:Array<string>;

  inputdatabases:Observable<any>;
  databases:Array<any>;
  selectedDatabase:string = "";
  tables:Array<any>;
  selectedTable:string = "";
  inputColumns:Array<ColumnSql>;

  @Output() emmitDatabaseChanged = new EventEmitter<String>();
  @Output() emmitShowColumns = new EventEmitter<DatabaseRequestConfig>();

  buttonBatchValues = [1, 5, 10, 25, 50, 100, 250, 500, 1000];

  constructor(private apiService:ApiService) { }

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

  batchQuery(numberOfQueries){
    console.log(numberOfQueries);
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
