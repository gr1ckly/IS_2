import {useState} from "react";
import FilterOption from "../../dtos/FilterOption";
import OperationType from "../../dtos/OperationType";
import PersonService from "../../services/PersonService";
import PersonTable from "../tables/PersonTable";
import TableState from "../../storage/states/TableState";
import styles from "../../styles/LessBirthday.module.css"

export default function LessBirthday() {
    const [tableState, setTableState] = useState<TableState | undefined>(undefined);
    const [birthday, setBirthday] = useState<string>("");
    const [message, setMessage] = useState("");

    const handleGet = async () => {
        if (birthday === "") {
            setMessage("Пожалуйста, введите корректный birthday");
            return
        }
        const currFilter: FilterOption = {fieldName: "birthday", operationType: OperationType.GREATER, value: birthday};
        const personNumber: number = await PersonService.getCount(currFilter);
        if (personNumber === -1) {
            setMessage(`Ошибка при выводе объектов с birthday < ${birthday} `);
            setBirthday("");
            setTableState(undefined);
            return
        } else if (personNumber === 0) {
            setMessage(`Нету объектов Person, родившихся позже ${birthday}`);
            setBirthday("");
            setTableState(undefined);
            return
        }
        setTableState({pageSize: 10, count:personNumber, currPage:1, filters: [currFilter]});
    }

    return (
        <div className={styles.container}>
            <span className={styles.label}>Вывести людей, день рождения которых позже заданного</span>
            <span className={styles.label}>Введите день рождения:</span>
            <input
                type="date"
                className={styles.input}
                min="0001-01-01"
                max="9999-12-31"
                required
                value={birthday}
                onChange={(e) => {
                    setBirthday(e.target.value || "")}}
            />

            <button className={styles.button} onClick={handleGet}>
                Показать Person
            </button>

            {message !== "" && <label className={styles.message}>{message}</label>}

            {tableState && (
                <div className={styles.tableWrapper}>
                    <PersonTable
                        tableState={tableState}
                        onChangeTableState={setTableState}
                    />
                </div>
            )}
        </div>
    );
}
