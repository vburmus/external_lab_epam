import {ILocation} from "./ILocation.js";
import {Employee} from "./Employee.js";

export class Location implements ILocation{
    employees: Employee[];
    city:string
    constructor(city:string) {
        this.employees = []
        this.city = city
    }
    public addPerson(employee: Employee): void {
        this.employees.push(employee)
    }

    public getCount(): number {
        return this.employees.length;
    }

    public getPerson(index: number): Employee {
        if(index >= this.employees.length){
            throw new Error("ArrayIndexOutOfBound")
        }
        return this.employees[index];
    }

    getEmployees(): Employee[] {
        return this.employees;
    }
}