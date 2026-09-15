const CheckBox = ({ toDo }) => {

  return (
    <div className="flex items-start justify-start p-2">
      <input
        className="scale-150 m-2 "
        type="checkbox"
        id=""
        checked={toDo?.completed}
        readOnly
      />
      <textarea
        className={` w-full outline-hidden resize-none overflow-hidden  text-lg`}
        name="toDos"
        id="toDosLabal"
        value={toDo?.title}
        readOnly
      />
    </div>
  );
};

export default CheckBox;
