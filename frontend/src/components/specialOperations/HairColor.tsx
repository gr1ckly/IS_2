import {ChangeEvent, useState} from "react";
import Color from "../../dtos/ColorEnum";
import PersonService from "../../services/PersonService";
import FilterOption from "../../dtos/FilterOption";
import OperationType from "../../dtos/OperationType";
import LocationService from "../../services/LocationService";
import LocationDTO from "../../dtos/LocationDTO";
import styles from "../../styles/HairColor.module.css";

export default function HairColor() {
    const [selectedColor, setSelectedColor] = useState<Color | undefined>(undefined);
    const [selectedLocationId, setSelectedLocationId] = useState<number | undefined>(undefined);
    const [message, setMessage] = useState("");
    const [locationIdMessage, setLocationIdMessage] = useState("");

    const handleCount = async () => {
        if (!selectedColor) {
            setMessage("Пожалуйста, выберите color");
            return
        }
        if (selectedLocationId === undefined) {
            setMessage("Пожалуйста, введите корректный location_id")
            return
        }
        console.log("Считаем всех с hairColor = ", selectedColor)
        const hairFilter: FilterOption = {fieldName: "hair_color", operationType: OperationType.EQUAL, value: Color[selectedColor].toString()};
        const locationFilter: FilterOption = {fieldName: "location_id", operationType: OperationType.EQUAL, value: selectedLocationId.toString()}
        const selectNumber: number = await PersonService.getCount(hairFilter, locationFilter);
        if (selectNumber === -1) {
            setMessage(`Ошибка при подсчете людей с hairColor = ${selectedColor.toString()} в локации с location_id = ${selectedLocationId.toString()}`);
        } else {
            setMessage(`Объектов с hairColor = ${selectedColor.toString()} и в локации с location_id = ${selectedLocationId} : ${selectNumber}`);
        }
        setSelectedColor(undefined);
        setSelectedLocationId(-1);
    }

    const handleChangeLocationId = async (e: ChangeEvent<HTMLInputElement>) => {
        const currLocationId: number = Number.parseInt(e.target.value);
        const currLocation: { coords: LocationDTO | undefined; count: number } = await LocationService.getLocationByID(currLocationId);
        if (currLocation.count < 1) {
            setLocationIdMessage(`Location с id = ${currLocationId} не существует`)
            setSelectedLocationId(undefined);
            return
        }
        setSelectedLocationId(currLocationId);
        setLocationIdMessage("");
    }

    return (
        <div className={styles.container}>
            <span className={styles.label}>Посчитать количество людей с определенным цветом волос в пределах заданной локации</span>
            <span className={styles.label}>Выберите цвет волос:</span>
            <select
                id="color"
                className={styles.select}
                value={selectedColor}
                onChange={(e) => {
                    setSelectedColor(e.target.value as unknown as Color);
                    if (e.target.value !== "") setMessage("");
                }}
            >
                <option value="">— выберите —</option>
                {Object.values(Color).filter((v) => typeof v === "string").map((n) => (
                    <option key={n} value={n}>
                        {n}
                    </option>
                ))}
            </select>

            <div className={styles.inputGroup}>
                <span className={styles.label}>Введите location_id:</span>
                <input
                    type="number"
                    className={styles.input}
                    onChange={handleChangeLocationId}
                />
                {locationIdMessage !== "" && (
                    <span className={styles.error}>{locationIdMessage}</span>
                )}
            </div>

            <button className={styles.button} onClick={handleCount}>
                Посчитать
            </button>

            {message !== "" && <label className={styles.message}>{message}</label>}
        </div>
    );
}