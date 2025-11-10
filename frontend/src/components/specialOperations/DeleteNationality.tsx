import Country from "../../dtos/CountryEnum";
import {useState} from "react";
import PersonService from "../../services/PersonService";
import FilterOption from "../../dtos/FilterOption";
import OperationType from "../../dtos/OperationType";
import styles from "../../styles/DeleteNationality.module.css";
import {useDispatch} from "react-redux";
import {COPY_STATE} from "../../consts/StateConsts";

export default function DeleteNationality() {
    const [deletedNationality, setDeletedNationality] = useState<Country | undefined>(undefined);
    const [message, setMessage] = useState("");
    const dispatcher = useDispatch();

    const handleDelete = async () => {
        if (deletedNationality === undefined) {
            setMessage("Пожалуйста, выберите nationality");
            return
        }
        else console.log("Удаляем всех с nationality =", deletedNationality)
        const currFilter: FilterOption = {fieldName: "nationality", operationType: OperationType.EQUAL, value: Country[deletedNationality].toString()};
        console.log(currFilter.value);
        const deleteNumber: number = await PersonService.deletePerson(currFilter);
        if (deleteNumber === undefined || deleteNumber === -1) {
            setMessage(`Ошибка при удалении объектов с nationality = ${deletedNationality.toString()} `);
        } else {
            setMessage(`Было удалено ${deleteNumber} объектов Person с nationality = ${deletedNationality.toString()}`);
        }
        setDeletedNationality(undefined);
        dispatcher({type: COPY_STATE});
    }

    return (
        <div className={styles.container}>
            <span className={styles.label}>Удалить людей с выбранной национальностью</span>
            <label htmlFor="nationality" className={styles.label}>
                Выберите национальность:
            </label>
            <select
                id="nationality"
                className={styles.select}
                value={deletedNationality ?? ""}
                onChange={(e) => {
                    setDeletedNationality(e.target.value as unknown as Country);
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

            <button className={styles.button} onClick={handleDelete}>
                Денацификация
            </button>

            {message && <label className={styles.message}>{message}</label>}
        </div>
    );
}
