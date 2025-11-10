import {useDispatch, useSelector} from "react-redux";
import {selectNotifications} from "../../storage/StateSelectors";
import {SET_NOTIFICATIONS} from "../../consts/StateConsts";
import styles from "../../styles/Notification.module.css";

export default function Notification() {
    const dispatcher = useDispatch();
    const stateNotifications: string[] = useSelector(selectNotifications);

    return (
        <div className={styles.container}>
            {Array.isArray(stateNotifications) && stateNotifications.map((message: string) => (
                <div key={message} className={styles.item}>
                    <label className={styles.message}>{message}</label>
                    <button
                        onClick={() => dispatcher({type: SET_NOTIFICATIONS, payload: stateNotifications.filter((val: string)=> val !== message)})}
                        className={styles.close}
                    >Закрыть</button>
                </div>
            ))}
        </div>
    )
}
