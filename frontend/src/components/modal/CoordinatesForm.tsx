import {useDispatch, useSelector} from "react-redux";
import {CLEAR_ALL, RELOAD_COORDINATES, SET_NOTIFICATIONS} from "../../consts/StateConsts";
import CoordinatesDTO from "../../dtos/CoordinatesDTO";
import {useState} from "react";
import CoordinatesService from "../../services/CoordinatesService";
import styles from "../../styles/CoordinatesForm.module.css"
import {selectNotifications} from "../../storage/StateSelectors";

interface Props {
    coordinates?: CoordinatesDTO;
}

export default function CoordinatesForm(props: Readonly<Props>) {
    const dispatcher = useDispatch();
    const notifications = useSelector(selectNotifications);
    const [newCoordinates, setCoordinates] = useState(
        props.coordinates ??
        {
            x: 0,
            y: 0,
        }
    )
    const [message, setMessage] = useState("");
    const [xMessage, setXMessage] = useState("");
    const [yMessage, setYMessage] = useState("");

    const handleCreate = async () => {
        if (xMessage || yMessage) {
            setMessage("Сначала введите корректные значения для всех полей");
            return;
        }
        if (newCoordinates.x <= -459) {
            setMessage("Некорректное значение поля x");
            return
        }
        if (newCoordinates.y <= -238) {
            setMessage("Некорректное значение поля y");
            return
        }
        const number = await CoordinatesService.createCoordinates(newCoordinates);
        if (number < 1) {
            setMessage("Ошибка при создании Coordinates");
            return
        }
        dispatcher({type: SET_NOTIFICATIONS, payload: [...notifications, `Создан Coordinates с id = ${number}`]});
        dispatcher({type: RELOAD_COORDINATES, payload: {}});
        setMessage("");
    }

    const handleUpdate = async () => {
        if (xMessage || yMessage) {
            setMessage("Сначала введите корректные значения для всех полей");
            return;
        }
        if (newCoordinates.x <= -459) {
            setMessage("Некорректное значение поля x");
            return
        }
        if (newCoordinates.y <= -238) {
            setMessage("Некорректное значение поля y");
            return
        }
        const number = await CoordinatesService.updateCoordinates(newCoordinates.id ? newCoordinates.id : 0, newCoordinates);
        if (number < 1) {
            setMessage(`Ошибка при обновлении Coordinates с id = ${newCoordinates.id}`);
            return
        }
        dispatcher({type: SET_NOTIFICATIONS, payload: [...notifications, `Обновлен Coordinates с id = ${newCoordinates.id}`]});
        dispatcher({type: RELOAD_COORDINATES, payload: {}});
        setMessage("");
    }

    return (
        <div className={styles.container}>
            <span className={styles.label}>Coordinates</span>
            <button
                className={styles.closeButton}
                onClick={() => dispatcher({ type: CLEAR_ALL })}
            >
                Закрыть
            </button>

            {newCoordinates.id && (
                <label className={styles.idLabel}>
                    Изменение Location с id: {newCoordinates.id}
                </label>
            )}

            <div className={styles.field}>
                <span className={styles.label}>X:</span>
                <input
                    className={styles.input}
                    type="text"
                    step="any"
                    required
                    min={-458.999999}
                    inputMode="decimal"
                    pattern="^-?\\d*(?:[.,]\\d*)?$"
                    value={Number.isFinite(newCoordinates.x as any) ? String(newCoordinates.x) : ""}
                    onChange={(e) => {
                        const raw = (e.currentTarget as HTMLInputElement).value;
                        const v = Number((raw || '').replace(',', '.'));
                        if (!Number.isFinite(v)) {
                            setXMessage("Некорректное значение X");
                            return;
                        } else if (v <= -459) {
                            setXMessage("Значение X должно быть > -459");
                            return;
                        }
                        setCoordinates({ ...newCoordinates, x: v })
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
                    step="1"
                    required
                    min={-237}
                    inputMode="numeric"
                    pattern="^-?\\d*$"
                    value={Number.isFinite(newCoordinates.y as any) ? String(newCoordinates.y) : ""}
                    onChange={(e) => {
                        const raw = (e.currentTarget as HTMLInputElement).value;
                        const v = Number((raw || '').replace(',', '.'));
                        if (!Number.isFinite(v)) {
                            setYMessage("Некорректное значение Y");
                            return;
                        } else if (!Number.isSafeInteger(v)) {
                            setYMessage("Некорректное значение Y");
                            return;
                        } else if (v < -237) {
                            setYMessage("Значение Y должно быть >= -237");
                            return;
                        }
                        setCoordinates({ ...newCoordinates, y: v as any })
                        setYMessage("");
                    }}
                />
                {yMessage && (<label className={styles.message}>{yMessage}</label>)}
            </div>

            {newCoordinates.id && (
                <button className={styles.actionButton} onClick={handleUpdate}>
                    Обновить
                </button>
            )}
            {newCoordinates.id === undefined && (
                <button className={styles.actionButton} onClick={handleCreate}>
                    Создать
                </button>
            )}

            {message !== "" && (
                <label className={styles.message}>{message}</label>
            )}
        </div>
    )
}
