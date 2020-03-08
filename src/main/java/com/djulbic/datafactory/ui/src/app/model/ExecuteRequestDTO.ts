import { DatabaseRequestConfig } from './DatabaseRequestConfig';
import { ColumnSql } from './ColumnSql';

export class ExecuteRequestDTO{
    config:DatabaseRequestConfig;
    columns:ColumnSql[];
}