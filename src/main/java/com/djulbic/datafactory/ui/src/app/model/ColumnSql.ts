import { MethodDTO } from './MethodDTO';

export class ColumnSql{
    checked:boolean;
    name:string;
    type:string;
    size:number;
    isForeignKey:boolean;
    isPrimaryKey:boolean;
    isNullable:boolean;
    method:MethodDTO;
}