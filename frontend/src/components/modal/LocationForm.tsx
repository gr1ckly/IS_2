import {useDispatch, useSelector} from "react-redux";
import {CLEAR_ALL, RELOAD_COORDINATES, RELOAD_LOCATIONS, SET_NOTIFICATIONS} from "../../consts/StateConsts";
import LocationDTO from "../../dtos/LocationDTO";
import {useState} from "react";
import LocationService from "../../services/LocationService";
import styles from "../../styles/LocationForm.module.css"
import {selectNotifications} from "../../storage/StateSelectors";

interface Props {
    location?: LocationDTO;
}

export default function LocationForm(props: Readonly<Props>) {
    const dispatcher = useDispatch();
    const notifications = useSelector(selectNotifications);
    const [newLocation, setLocation] = useState(
        props.location ??
        {
            x: 0,
            y: 0,
            name: "",
        }
    )
    const [nameMessage, setNameMessage] = useState("");
    const [xMessage, setXMessage] = useState("");
    const [yMessage, setYMessage] = useState("");
    const [message, setMessage] = useState("");

    const handleCreate = async () => {
        if (message || nameMessage !== "" || xMessage !== "" || yMessage !== "") {
            setMessage("Сначала введите корректные значения для всех полей");
            return;
        }
        if (newLocation.name.length > 871) {
            setMessage("Некорректное значение поля name");
            return
        }
        const number = await LocationService.createLocation(newLocation);
        if (number < 1) {
            setMessage("Ошибка при создании Location");
            return
        }
        dispatcher({type: SET_NOTIFICATIONS, payload: [...notifications, `Создан Location с id = ${number}`]});
        setMessage("");
    }

    const handleUpdate = async () => {
        if (message || nameMessage !== "" || xMessage !== "" || yMessage !== "") {
            setMessage("Сначала введите корректные значения для всех полей");
            return;
        }
        if (newLocation.name.length > 871) {
            setMessage("Некорректное значение поля name");
            return
        }
        const number = await LocationService.updateLocation(newLocation.id ? newLocation.id : 0, newLocation);
        if (number < 1) {
            setMessage(`Ошибка при обновлении Location с id = ${newLocation.id}`);
            return
        }
        dispatcher({type: SET_NOTIFICATIONS, payload: [...notifications, `Обновлен Location с id = ${newLocation.id}`]});
        setMessage("");
    }

    return (
        <div className={styles.container}>
            <span className={styles.label}>Location</span>
            <button
                className={styles.closeButton}
                onClick={() => dispatcher({ type: CLEAR_ALL })}
            >
                Закрыть
            </button>

            {newLocation.id && (
                <label className={styles.idLabel}>
                    Изменение Location с id: {newLocation.id}
                </label>
            )}

            <div className={styles.field}>
                <span className={styles.label}>X:</span>
                <input
                    className={styles.input}
                    type="text"
                    step="any"
                    required
                    inputMode="decimal"
                    pattern="^-?\\d*(?:[.,]\\d*)?$"
                    value={Number.isFinite(newLocation.x) ? String(newLocation.x) : ""}
                    onChange={(e) => {
                        const raw = (e.currentTarget as HTMLInputElement).value;
                        const v = Number((raw || '').replace(',', '.'));
                        if (!Number.isFinite(v)) {
                            setXMessage("Некорректное значение X");
                            return;
                        }
                        setLocation({...newLocation, x: v});
                        setXMessage("");
                    }}
                />
                {xMessage && (<label className={styles.message}>{xMessage}</label>)}
            </div>

            <div className={styles.field}>
                <span className={styles.label}>Y:</span>
                <input
                    className={styles.input}
                    type="text"
                    step="any"
                    required
                    inputMode="decimal"
                    pattern="^-?\\d*(?:[.,]\\d*)?$"
                    value={Number.isFinite(newLocation.y) ? String(newLocation.y) : ""}
                    onChange={(e) => {
                        const raw = (e.currentTarget as HTMLInputElement).value;
                        const v = Number((raw || '').replace(',', '.'));
                        const MAX_F32 = 3.4028235e38;
                        if (!Number.isFinite(v) || v >= MAX_F32 || v <= -MAX_F32) {
                            setYMessage("Некорректное значение Y");
                            return;
                        }
                        setLocation({...newLocation, y: v});
                        setYMessage("");
                    }}
                />
                {yMessage && (<label className={styles.message}>{yMessage}</label>)}
            </div>

            <div className={styles.field}>
                <span className={styles.label}>Name (необязательно):</span>
                <input
                    className={styles.input}
                    type="text"
                    maxLength={871}
                    value={newLocation.name}
                    onChange={(e) => {
                        const value = e.target.value;
                        setLocation({...newLocation, name:value})
                        if (/^-?\d+(\.\d+)?$/.test(value.trim())) {
                            setNameMessage("Поле name не должно быть числом");
                        } else if (value.length > 871) {
                            setNameMessage("Длина поля name не должна превышать 871 символ")
                        } else {
                            setNameMessage("");
                        }
                    }}
                />
                {nameMessage && (
                    <label className={styles.message}>{nameMessage}</label>
                )}
            </div>

            {newLocation.id ? (
                <button className={styles.actionButton} onClick={handleUpdate}>
                    Обновить
                </button>
            ) : (
                <button className={styles.actionButton} onClick={handleCreate}>
                    Создать
                </button>
            )}

            {message && <label className={styles.message}>{message}</label>}
        </div>
    )
}
