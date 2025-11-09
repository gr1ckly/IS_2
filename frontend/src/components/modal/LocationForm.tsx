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
    const [message, setMessage] = useState("");

    const handleCreate = async () => {
        if (message) return;
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
        if (message) return;
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
                    type="number"
                    step="any"
                    value={newLocation.x}
                    onChange={(e) => {
                        const v = (e.currentTarget as HTMLInputElement).valueAsNumber;
                        if (!Number.isFinite(v)) {
                            setMessage("Некорректное значение X");
                        } else {
                            setMessage("");
                        }
                        setLocation({
                            ...newLocation,
                            x: v,
                        })
                    }}
                />
            </div>

            <div className={styles.field}>
                <span className={styles.label}>Y:</span>
                <input
                    className={styles.input}
                    type="number"
                    step="any"
                    value={newLocation.y}
                    onChange={(e) => {
                        const v = (e.currentTarget as HTMLInputElement).valueAsNumber;
                        if (!Number.isFinite(v)) {
                            setMessage("Некорректное значение Y");
                        } else {
                            setMessage("");
                        }
                        setLocation({
                            ...newLocation,
                            y: v,
                        })
                    }}
                />
            </div>

            <div className={styles.field}>
                <span className={styles.label}>Name (необязательно):</span>
                <input
                    className={styles.input}
                    type="text"
                    value={newLocation.name}
                    onChange={(e) => {
                        const value = e.target.value;
                        if (/^-?\d+(\.\d+)?$/.test(value.trim())) {
                            setNameMessage("Поле name не должно быть числом");
                        } else if (value.length > 871) {
                            setNameMessage("Длина поля name не должна превышать 871 символ")
                        } else {
                            setNameMessage("");
                        }
                        setLocation({...newLocation, name:value})
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
