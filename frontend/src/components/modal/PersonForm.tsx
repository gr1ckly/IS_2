import PersonDTO from "../../dtos/PersonDTO";
import {useDispatch, useSelector} from "react-redux";
import {CLEAR_ALL, SET_NOTIFICATIONS} from "../../consts/StateConsts";
import {ChangeEvent, useState} from "react";
import LocationDTO from "../../dtos/LocationDTO";
import LocationService from "../../services/LocationService";
import Color from "../../dtos/ColorEnum";
import Country from "../../dtos/CountryEnum";
import CoordinatesDTO from "../../dtos/CoordinatesDTO";
import CoordinatesService from "../../services/CoordinatesService";
import PersonService from "../../services/PersonService";
import styles from "../../styles/PersonForm.module.css";
import {selectNotifications} from "../../storage/StateSelectors";

interface Props {
    person?: PersonDTO;
}

export default function PersonForm(props: Readonly<Props>) {
    const MAX_FLOAT32 = 3.4028235e38;
    const dispatcher = useDispatch();
    const notifications = useSelector(selectNotifications);
    const [currPerson, setCurrPerson] = useState<PersonDTO>(
        props.person ?? {
            name: "",
            coordinatesId: 0,
            hairColor: Color.BROWN,
            height: 0,
            birthday: "",
            nationality: Country.RUSSIA,
        },
    );
    const [nameMessage, setNameMessage] = useState<string>("");
    const [locationIdMessage, setLocationIdMessage] = useState<string>("");
    const [coordinatesIdMessage, setCoordinatesIdMessage] = useState<string>("");
    const [eyeColorMessage, setEyeColorMessage] = useState<string>("");
    const [heightMessage, setHeightMessage] = useState<string>("");
    const [weightMessage, setWeightMessage] = useState<string>("");
    const [message, setMessage] = useState<string>("");
    const [currCoordinatesId, setCurrCoordinatesId] = useState<number | undefined>(currPerson.coordinatesId);

    const handleChangeCoordinatesId = async (e: ChangeEvent<HTMLInputElement>) => {
        const raw = Number.parseInt(e.target.value);
        const MAX_SAFE = Number.MAX_SAFE_INTEGER;
        const value = Number.isNaN(raw) ? raw : Math.min(raw, MAX_SAFE);
        setCurrCoordinatesId(Number.isNaN(value) ? undefined : value);

        if (!Number.isFinite(value) || Number.isNaN(value) || value <= 0) {
            setCoordinatesIdMessage("Некорректное значение coordinates_id (должно быть >= 1)");
            return;
        }

        const currCoordinates: { coords: CoordinatesDTO | undefined; count: number } = await CoordinatesService.getCoordinatesByID(value);
        if (currCoordinates.count < 1) {
            setCoordinatesIdMessage(`Coordinates с id = ${value} не найден`);
            return;
        }

        setCurrPerson({ ...currPerson, coordinatesId: value });
        setCoordinatesIdMessage("");
    };

    const handleChangeLocationId = async (e: ChangeEvent<HTMLInputElement>) => {
        if (e.target.value === "") {
            setCurrPerson({ ...currPerson, locationId: undefined });
            setLocationIdMessage("");
            return;
        }
        const raw = Number.parseInt(e.target.value);
        const MAX_SAFE = Number.MAX_SAFE_INTEGER;
        const currLocationId = Number.isNaN(raw) ? raw : Math.min(raw, MAX_SAFE);
        if (!Number.isFinite(currLocationId) || Number.isNaN(currLocationId) || currLocationId <= 0) {
            setLocationIdMessage("Некорректное значение location_id (должно быть >= 1)");
            return;
        }
        const currLocation: { coords: LocationDTO | undefined; count: number } = await LocationService.getLocationByID(currLocationId);
        if (currLocation.count < 1) {
            setLocationIdMessage(`Location с id = ${currLocationId} не найден`);
            setCurrPerson({ ...currPerson, locationId: currLocationId });
            return;
        }
        setCurrPerson({ ...currPerson, locationId: currLocationId });
        setLocationIdMessage("");
    };

    const handleCreate = async () => {
        if (nameMessage !== "" || locationIdMessage !== "" || coordinatesIdMessage !== "" || eyeColorMessage !== "" || weightMessage !== "" || heightMessage !== "" || currPerson.height <= 0) {
            setMessage("Введите корректные значения для всех полей");
            return;
        }
        if (/^-?\d+(\.\d+)?$/.test(currPerson.name.trim()) || currPerson.name === "") {
            setMessage("Поле name не должно быть пустым и не может быть числом");
            return;
        }
        if (currPerson.coordinatesId === 0) {
            setMessage("Поле coordinates_id не должно быть пустым");
            return;
        }
        if (currPerson.height === 0) {
            setMessage("Поле height должно быть > 0");
            return;
        }
        if (currPerson.birthday === "") {
            setMessage("Поле birthday не должно быть пустым");
            return;
        }
        const number = await PersonService.createPerson(currPerson);
        if (number < 1) {
            setMessage("Не удалось создать Person");
            return;
        }
        setMessage("");
        dispatcher({type: SET_NOTIFICATIONS, payload: [...notifications, `Создан Person с id = ${number}`]});
    };

    const handleUpdate = async () => {
        if (nameMessage !== "" || locationIdMessage !== "" || coordinatesIdMessage !== "" || eyeColorMessage !== "" || weightMessage !== "" || heightMessage !== "" || currPerson.height <= 0) {
            setMessage("Введите корректные значения для всех полей");
            return;
        }
        if (/^-?\d+(\.\d+)?$/.test(currPerson.name.trim()) || currPerson.name === "") {
            setMessage("Поле name не должно быть пустым и не может быть числом");
            return;
        }
        if (currPerson.coordinatesId === 0) {
            setMessage("Поле coordinates_id не должно быть пустым");
            return;
        }
        if (currPerson.height === 0) {
            setMessage("Поле height должно быть > 0");
            return;
        }
        if (currPerson.birthday === "") {
            setMessage("Поле birthday не должно быть пустым");
            return;
        }
        const number = await PersonService.updatePerson(currPerson.id ? currPerson.id : 0, currPerson);
        if (number < 1) {
            setMessage(`Не удалось обновить Person с id = ${currPerson.id}`);
            return;
        }
        setMessage("");
        dispatcher({type: SET_NOTIFICATIONS, payload: [...notifications, `Обновлен Person с id = ${currPerson.id}`]});
    };

    return (
        <div className={styles.container}>
            <span className={styles.label}>Person</span>
            <button className={styles.closeButton} onClick={() => dispatcher({ type: CLEAR_ALL })}>
                Закрыть
            </button>

            {currPerson.id && (
                <label className={styles.idLabel}>Текущий id: {currPerson.id}</label>
            )}

            <div className={styles.field}>
                <span className={styles.label}>name:</span>
                <input
                    type="text"
                    className={styles.input}
                    maxLength={1000}
                    required
                    value={currPerson.name ?? ""}
                    onChange={(e) => {
                        const value = e.target.value;
                        if (value === "") {
                            setNameMessage("Поле name не должно быть пустым");
                        } else if (/^-?\d+(\.\d+)?$/.test(value.trim())) {
                            setNameMessage("Поле name не должно состоять только из числа");
                        } else if (value.length > 1000) {
                            setNameMessage("Длина поля name не должна превышать 1000 символов");
                        } else {
                            setNameMessage("");
                        }
                        setCurrPerson({ ...currPerson, name: value });
                    }}
                />
                {nameMessage && <label className={styles.message}>{nameMessage}</label>}
            </div>

            <div className={styles.field}>
                <span className={styles.label}>coordinates_id:</span>
                <input
                    type="text"
                    step="1"
                    required
                    min={1}
                    inputMode="numeric"
                    pattern="^\\d*$"
                    className={styles.input}
                    value={currCoordinatesId ?? ""}
                    onChange={handleChangeCoordinatesId}
                />
                {coordinatesIdMessage && (
                    <label className={styles.message}>{coordinatesIdMessage}</label>
                )}
            </div>

            <div className={styles.field}>
                <span className={styles.label}>eyeColor(необязательно):</span>
                <select
                    className={styles.select}
                    value={currPerson.eyeColor ?? ""}
                    onChange={(e) => {
                        const v = e.target.value;
                        setCurrPerson({ ...currPerson, eyeColor: v === "" ? undefined : (v as unknown as Color) });
                        if (v) setEyeColorMessage("");
                    }}
                >
                    <option value=""></option>
                    {Object.values(Color)
                        .filter((v) => typeof v === "string")
                        .map((color) => (
                            <option key={color} value={color}>
                                {color}
                            </option>
                        ))}
                </select>
                {eyeColorMessage && (
                    <label className={styles.message}>{eyeColorMessage}</label>
                )}
            </div>

            <div className={styles.field}>
                <span className={styles.label}>hairColor:</span>
                <select
                    className={styles.select}
                    value={currPerson.hairColor ?? ""}
                    onChange={(e) =>
                        setCurrPerson({ ...currPerson, hairColor: e.target.value as unknown as Color })
                    }
                >
                    {Object.values(Color)
                        .filter((v) => typeof v === "string")
                        .map((color) => (
                            <option key={color} value={color}>
                                {color}
                            </option>
                        ))}
                </select>
            </div>

            <div className={styles.field}>
                <span className={styles.label}>location_id(необязательно):</span>
                <input
                    type="text"
                    step="1"
                    inputMode="numeric"
                    pattern="^\\d*$"
                    className={styles.input}
                    value={currPerson.locationId ?? ""}
                    onChange={handleChangeLocationId}
                />
                {locationIdMessage && (
                    <label className={styles.message}>{locationIdMessage}</label>
                )}
            </div>

            <div className={styles.field}>
                <span className={styles.label}>height:</span>
                <input
                    type="text"
                    step="any"
                    required
                    min={0.0000001}
                    max={3.4028235e38}
                    inputMode="decimal"
                    pattern="^\\d*(?:[.,]\\d*)?$"
                    className={styles.input}
                    value={Number.isFinite(currPerson.height ?? (Number.NaN as any)) ? String(currPerson.height) : ""}
                    onChange={(e) => {
                        const value = Number.parseFloat(e.target.value.replace(',', '.'));
                        if (!Number.isFinite(value) || value >= MAX_FLOAT32 || value <= -MAX_FLOAT32) {
                            setHeightMessage("Некорректное значение height");
                            return;
                        }
                        setCurrPerson({ ...currPerson, height: value });
                        setHeightMessage(value > 0 ? "" : "Поле height должно быть больше 0");
                    }}
                />
                {heightMessage && (
                    <label className={styles.message}>{heightMessage}</label>
                )}
            </div>

            <div className={styles.field}>
                <span className={styles.label}>birthday:</span>
                <input
                    type="date"
                    required
                    className={styles.input}
                    min="0001-01-01"
                    max="9999-12-31"
                    value={currPerson.birthday ? currPerson.birthday.split("T")[0] : ""}
                    onChange={(e) => {
                        const dateString = e.target.value;
                        setCurrPerson({
                            ...currPerson,
                            birthday: dateString ? `${dateString}T00:00:00.000` : "",
                        });
                    }}
                />
            </div>

            <div className={styles.field}>
                <span className={styles.label}>weight(необязательно):</span>
                <input
                    type="text"
                    step="any"
                    min={0.0000001}
                    max={3.4028235e38}
                    inputMode="decimal"
                    pattern="^\\d*(?:[.,]\\d*)?$"
                    className={styles.input}
                    value={Number.isFinite(currPerson.weight ?? (Number.NaN as any)) ? String(currPerson.weight) : ""}
                    onChange={(e) => {
                        const raw = e.target.value;
                        if (raw === "") {
                            setCurrPerson({ ...currPerson, weight: undefined });
                            setWeightMessage("");
                            return;
                        }
                        const value = Number.parseFloat(raw.replace(',', '.'));
                        if (!Number.isFinite(value) || value >= MAX_FLOAT32 || value <= -MAX_FLOAT32) {
                            setWeightMessage("Некорректное значение weight");
                            return;
                        }
                        if (value <= 0 ){
                            setWeightMessage("Поле weight должно быть больше 0");
                            return
                        }
                        setCurrPerson({ ...currPerson, weight: value });
                        setWeightMessage("");
                    }}
                    onKeyDown={(e) => {
                        const k = e.key;
                        if (k === 'e' || k === 'E' || k === '+' || k === '-') {
                            e.preventDefault();
                        }
                    }}
                    onPaste={(e) => {
                        const t = e.clipboardData.getData('text');
                        if (/[eE+\-]/.test(t)) {
                            e.preventDefault();
                        }
                    }}
                />
                {weightMessage && (
                    <label className={styles.message}>{weightMessage}</label>
                )}
            </div>

            <div className={styles.field}>
                <span className={styles.label}>nationality:</span>
                <select
                    className={styles.select}
                    value={currPerson.nationality ?? ""}
                    onChange={(e) =>
                        setCurrPerson({ ...currPerson, nationality: e.target.value as unknown as Country })
                    }
                >
                    {Object.values(Country)
                        .filter((v) => typeof v === "string")
                        .map((country) => (
                            <option key={country} value={country}>
                                {country}
                            </option>
                        ))}
                </select>
            </div>

            {currPerson.id ? (
                <button className={styles.actionButton} onClick={handleUpdate}>
                    Обновить
                </button>
            ) : (
                <button className={styles.actionButton} onClick={handleCreate}>
                    Создать Person
                </button>
            )}

            {message && <label className={styles.message}>{message}</label>}
        </div>
    );
}
