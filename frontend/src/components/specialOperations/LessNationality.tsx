import {useState} from "react";
import Country from "../../dtos/CountryEnum";
import FilterOption from "../../dtos/FilterOption";
import OperationType from "../../dtos/OperationType";
import PersonService from "../../services/PersonService";
import styles from "../../styles/LessNationality.module.css"

export default function LessNationality() {
    const [selectedNationality, setSelectedNationality] = useState<Country | undefined>(undefined);
    const [message, setMessage] = useState("");

    const handleCount = async () => {
        if (selectedNationality === undefined) {
            setMessage("Пожалуйста, выберите nationality");
            return
        }
        else console.log("Считаем всех с nationality <", selectedNationality)
        const currFilter: FilterOption = {fieldName: "nationality", operationType: OperationType.LESS, value: Country[selectedNationality].toString()};
        console.log(currFilter.value);
        const selectNumber: number = await PersonService.getCount(currFilter);
        if (selectNumber === -1) {
            setMessage(`Ошибка при подсчете объектов с nationality < ${selectedNationality.toString()} `);
        } else {
            setMessage(`Объектов с nationality < ${selectedNationality.toString()} : ${selectNumber}`);
        }
        setSelectedNationality(undefined);
    }

    return (
        <div className={styles.container}>
            <span className={styles.label}>Посчитать людей с национальностью меньше, чем заданная</span>
            <span className={styles.label}>Выберите национальность:</span>
            <select
                id="nationality"
                className={styles.select}
                value={selectedNationality}
                onChange={(e) => {
                    setSelectedNationality(e.target.value as unknown as Country);
                    if (e.target.value !== "") setMessage("");
                }}
            >
                <option value="">— выберите —</option>
                {Object.values(Country).filter((v) => typeof v === "string").map((n) => (
                    <option key={n} value={n}>
                        {n}
                    </option>
                ))}
            </select>

            <button className={styles.button} onClick={handleCount}>
                Посчитать
            </button>

            {message !== "" && <label className={styles.message}>{message}</label>}
        </div>
    );
}