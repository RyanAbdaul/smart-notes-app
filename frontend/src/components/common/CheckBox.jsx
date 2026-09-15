const CheckBox = ({ toDo }) => {
  //   const [toDos, setTodo] = useState([{ value: null, complete: false }]);

  return (
    <div className="flex items-start justify-start p-2">
      <input
        className="scale-150 m-2 "
        type="checkbox"
        id=""
        checked={toDo?.completed}
        readOnly
        // onChange={(e) => setTodo([{ complete: e.target.checked }])}
      />
      <textarea
        className={` w-full outline-hidden resize-none overflow-hidden  text-lg`}
        name="toDos"
        id="toDosLabal"
        value={toDo?.title}
        readOnly
        // onChange={(e) => setTodo([{ value: e.target.value }])}
      />
    </div>
  );
};

export default CheckBox;
