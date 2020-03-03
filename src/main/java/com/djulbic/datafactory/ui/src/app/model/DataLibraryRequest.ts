import { ColumnSql } from './ColumnSql';
import { DatabaseRequestConfig } from './DatabaseRequestConfig';
import { DataLibraryConfiguration } from './DataLibraryConfiguration';

export class DataLibraryRequest{
    numberOfRequests:number;

    databaseConfiguration:DatabaseRequestConfig;
    dataLibraryConfiguration:DataLibraryConfiguration;

    entries: ColumnSql[];
}