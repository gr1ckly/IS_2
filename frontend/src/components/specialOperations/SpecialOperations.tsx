import DeleteNationality from "./DeleteNationality";
import LessNationality from "./LessNationality";
import EyesColor from "./EyesColor";
import HairColor from "./HairColor";
import LessBirthday from "./LessBirthday";

import styles from "../../styles/SpecialOperations.module.css";

export default function SpecialOperationsComponent () {
   return (
       <div className={styles.container}>
           <DeleteNationality />
           <LessNationality />
           <EyesColor />
           <HairColor />
           <div className={styles.fullRow}>
               <LessBirthday />
           </div>
       </div>
    )
}
